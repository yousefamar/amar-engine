uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

mat4 getModelViewProjectionMatrix() {
	return projectionMatrix * viewMatrix * modelMatrix;
}


varying vec2 texCoord;

void updateTexCoords() {
	texCoord = gl_MultiTexCoord0.st;
}


varying vec3 lightPos;
varying vec3 vertNormal;
//uniform samplerCube shadowMap;

void updateLighting() {
	/* NB: This will only work for uniform scaling for obvious reasons but supporting non-uniform scaling is overkill. */
	vertNormal = normalize(modelMatrix * vec4(gl_Normal, 0.0)).xyz;
	lightPos = normalize(gl_LightSource[0].position.xyz);
}


varying vec4 shadowCoord;

void updateShadow() {
	/* Transform shadow fragment world coordinate with shadowMap matrix. */
	shadowCoord = gl_TextureMatrix[7] * modelMatrix * gl_Vertex;
}