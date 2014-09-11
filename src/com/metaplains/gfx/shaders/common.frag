uniform sampler2D texture;
varying vec2 texCoord;

vec4 getTexture() {
	return texture2D(texture, texCoord);
}


uniform bool isLightingEnabled;
varying vec3 lightPos;
varying vec3 vertNormal;

float getDiffuse() {
	//TODO: Ambient again.
	return isLightingEnabled?0.2+max(dot(vertNormal, lightPos), 0.0):1.0;
}


uniform sampler2DShadow shadowMap;
uniform float shadowMapRes;
varying vec4 shadowCoord;

float lookup(vec2 offset) {
	/* Scale offCord by shadowCoord.w because shadow2DProj does a perspective division */
	vec4 offCoord = shadowCoord + vec4((offset.x/shadowMapRes)*shadowCoord.w, (offset.y/shadowMapRes)*shadowCoord.w, 0.0005, 0.0);
	/* Cut off outside shadow map to prevent stretching */
	if (offCoord.x < 0.0 || offCoord.y < 0.0 || offCoord.x/shadowCoord.w > 1.0 || offCoord.y/shadowCoord.w > 1.0)
		return 1.0;
	else
		return shadow2DProj(shadowMap, offCoord).w;
}

float getShadow() {
	float shadow = 1.0;
	/* Avoid counter shadow */
	if (isLightingEnabled && shadowCoord.w > 1.0) {
		//shadow = max(lookup(vec2(0.0,0.0)), 0.5);
		shadow = 1.0-((1.0-((lookup(vec2(0,1)) + lookup(vec2(1,0)) + lookup(vec2(0,-1)) + lookup(vec2(-1,0)))/4.0))/2.0);
	}
	
	return shadow;
}