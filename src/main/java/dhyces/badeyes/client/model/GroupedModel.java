package dhyces.badeyes.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class GroupedModel implements IUnbakedGeometry<GroupedModel> {
    private final List<BlockElement> elements;
    private final List<ElementGroup> groups;

    public GroupedModel(List<BlockElement> elements, List<ElementGroup> groups) {
        this.elements = elements;
        this.groups = groups;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {

        List<BakedQuad> unculledQuads = new ArrayList<>();
        Map<Direction, List<BakedQuad>> culledQuads = new EnumMap<>(Direction.class);

        QuadGroup[] groupArr = new QuadGroup[elements.size()];
        Map<String, QuadGroup> finishedGroups = new HashMap<>();

        for (ElementGroup group : groups) {
            IntStream stream = resolveIndices(group);
            QuadGroup quadGroup = QuadGroup.create();
            finishedGroups.put(group.name, quadGroup);
            stream.forEach(value -> groupArr[value] = quadGroup);
        }

        for (int i = 0; i < elements.size(); i++) {
            BlockElement element = elements.get(i);
            QuadGroup group = groupArr[i];
            for (Direction dir : element.faces.keySet()) {
                BlockElementFace face = element.faces.get(dir);
                TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture));
                BakedQuad quad = BlockModel.bakeFace(element, face, sprite, dir, modelState, modelLocation);
                if (face.cullForDirection == null) {
                    group.addUnculled(quad);
                    unculledQuads.add(quad);
                } else {
                    Direction transformedDir = modelState.getRotation().rotateTransform(face.cullForDirection);
                    group.addCulled(transformedDir, quad);
                    culledQuads.computeIfAbsent(transformedDir, direction -> new ArrayList<>()).add(quad);
                }
            }
        }

        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

        return new Baked(unculledQuads, culledQuads, context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), particle, context.getTransforms(), overrides, context.getRenderType(context.getRenderTypeHint()), finishedGroups);
    }

    private IntStream resolveIndices(ElementGroup group) {
        IntStream.Builder streamBuilder = IntStream.builder();
        for (GroupOrInt groupOrInt : group.children()) {
            if (!groupOrInt.isGroup) {
                streamBuilder.add(groupOrInt.elementIndex);
            } else {
                resolveIndices(groupOrInt.group).forEach(streamBuilder::add);
            }
        }
        return streamBuilder.build();
    }

    public static class Baked implements BakedModel {

        private final List<BakedQuad> unculledQuads;
        private final Map<Direction, List<BakedQuad>> culledQuads;
        private final boolean hasAmbientOcclusion;
        private final boolean usesBlockLight;
        private final boolean isGui3d;
        private final TextureAtlasSprite particleIcon;
        private final ItemTransforms transforms;
        private final ItemOverrides overrides;
        private final RenderTypeGroup renderTypes;
        private final Map<String, QuadGroup> groups;

        public Baked(List<BakedQuad> unculledQuads, Map<Direction, List<BakedQuad>> culledQuads, boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, RenderTypeGroup renderTypes, Map<String, QuadGroup> groups) {
            this.unculledQuads = unculledQuads;
            this.culledQuads = culledQuads;
            this.hasAmbientOcclusion = hasAmbientOcclusion;
            this.usesBlockLight = usesBlockLight;
            this.isGui3d = isGui3d;
            this.particleIcon = particleIcon;
            this.transforms = transforms;
            this.overrides = overrides;
            this.renderTypes = renderTypes;
            this.groups = groups;
        }

        public QuadGroup getGroup(String name) {
            return groups.get(name);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
            List<BakedQuad> quads = pDirection == null ? unculledQuads : culledQuads.get(pDirection);
            return quads == null ? List.of() : quads;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return hasAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight() {
            return usesBlockLight;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particleIcon;
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
            return List.of(fabulous ? renderTypes.entityFabulous() : renderTypes.entity());
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return ChunkRenderTypeSet.of(renderTypes.block());
        }
    }

    public static class QuadGroup {

        private final List<BakedQuad> unculledQuads;
        private final Map<Direction, List<BakedQuad>> culledQuads;

        public QuadGroup(List<BakedQuad> unculledQuads, Map<Direction, List<BakedQuad>> culledQuads) {
            this.unculledQuads = unculledQuads;
            this.culledQuads = culledQuads;
        }

        public static QuadGroup create() {
            return new QuadGroup(new ArrayList<>(), new EnumMap<>(Direction.class));
        }

        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? unculledQuads : culledQuads.get(direction);
        }

        public void addUnculled(BakedQuad quad) {
            unculledQuads.add(quad);
        }

        public void addCulled(Direction dir, BakedQuad quad) {
            culledQuads.computeIfAbsent(dir, direction -> new ArrayList<>()).add(quad);
        }
    }

    public record GroupOrInt(@Nullable ElementGroup group, int elementIndex, boolean isGroup) {
        public static final Codec<GroupOrInt> CODEC = ExtraCodecs.lazyInitializedCodec(() -> Codec.either(ElementGroup.CODEC, Codec.INT).xmap(
                either -> either.map(
                        elementGroup -> new GroupOrInt(elementGroup, -1, true),
                        integer -> new GroupOrInt(null, integer, false)
                ),
                groupOrInt -> groupOrInt.isGroup ? Either.left(groupOrInt.group) : Either.right(groupOrInt.elementIndex)
        ));
    }

    public record ElementGroup(String name, Vector3f origin, int color, List<GroupOrInt> children) {
        public static final Codec<ElementGroup> CODEC = ExtraCodecs.lazyInitializedCodec(() -> RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("name").forGetter(elementGroup -> elementGroup.name),
                        ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter(elementGroup -> elementGroup.origin),
                        Codec.INT.fieldOf("color").forGetter(elementGroup -> elementGroup.color),
                        Codec.list(GroupOrInt.CODEC).fieldOf("children").forGetter(elementGroup -> elementGroup.children)
                ).apply(instance, ElementGroup::new)
        ));


    }

    public static class Loader implements IGeometryLoader<GroupedModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public GroupedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("elements")) {
                throw new JsonParseException("A grouped model must have an \"elements\" member.");
            }

            List<BlockElement> blockElements = new ArrayList<>();
            for (JsonElement element : jsonObject.getAsJsonArray("elements")) {
                blockElements.add(deserializationContext.deserialize(element, BlockElement.class));
            }

            List<ElementGroup> groups = new ArrayList<>();

            for (JsonElement element : jsonObject.getAsJsonArray("groups")) {
                groups.add(ElementGroup.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false, s -> {}));
            }

            return new GroupedModel(blockElements, groups);
        }
    }
}
