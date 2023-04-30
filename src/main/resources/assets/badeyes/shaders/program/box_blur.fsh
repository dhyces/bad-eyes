#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform ivec2 Params;

out vec4 fragColor;

void main() {
    vec2 texSize = textureSize(DiffuseSampler, 0);
    int size = Params.x;
    int separation = Params.y;
    for (int i = -size; i <= size; i++) {
        for (int j = -size; j <= size; j++) {
            fragColor += texture(DiffuseSampler, (gl_FragCoord.xy + (vec2(i, j) * separation)) / texSize);
        }
    }
    fragColor /= pow(size * 2 + 1, 2);
}
