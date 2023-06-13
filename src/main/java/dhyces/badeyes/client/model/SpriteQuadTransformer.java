package dhyces.badeyes.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.model.IQuadTransformer;

public class SpriteQuadTransformer implements IQuadTransformer {

    private final TextureAtlasSprite sprite;

    private SpriteQuadTransformer(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public static SpriteQuadTransformer create(TextureAtlasSprite sprite) {
        return new SpriteQuadTransformer(sprite);
    }

    @Override
    public void processInPlace(BakedQuad quad) {
        if (quad.getSprite() == sprite) {
            return;
        }
        TextureAtlasSprite original = quad.getSprite();
        int sections = quad.getVertices().length / 8;
        int[] vertices = quad.getVertices();
        for (int v = 0; v < sections; v++) {
            float[] arr = new float[2];
            unpackUvs(vertices, arr, v);
            float relU = original.getUOffset(arr[0]);
            float relV = original.getVOffset(arr[1]);
            arr[0] = sprite.getU(relU);
            arr[1] = sprite.getV(relV);
            packUvs(vertices, arr, v);
        }
    }

    private void unpackUvs(int[] vertices, float[] uvDest, int v) {
        int offset = v * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        uvDest[0] = Float.intBitsToFloat(vertices[offset]);
        uvDest[1] = Float.intBitsToFloat(vertices[offset + 1]);
    }

    private void packUvs(int[] vertices, float[] uvDest, int v) {
        int offset = v * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertices[offset] = Float.floatToRawIntBits(uvDest[0]);
        vertices[offset + 1] = Float.floatToRawIntBits(uvDest[1]);
    }
}
