vec4 getTexture();
float getDiffuse();
//float getShadow();

void main() {
	//TODO: Add ambient lighting support.
	gl_FragColor = getTexture() * gl_Color * getDiffuse();// * getShadow();
}