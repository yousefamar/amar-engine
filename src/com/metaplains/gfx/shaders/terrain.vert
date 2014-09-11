uniform mat4 modelMatrix;
varying vec4 worldCoords;

mat4 getModelViewProjectionMatrix();
void updateLighting();
//void updateShadow();

void main() {
	updateLighting();
	//updateShadow();
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	worldCoords = modelMatrix * gl_Vertex;

	//gl_FrontColor = gl_Color;
	gl_Position = getModelViewProjectionMatrix() * gl_Vertex;
}