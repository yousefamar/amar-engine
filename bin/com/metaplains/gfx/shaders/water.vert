uniform float waveTime;
uniform float waveWidth;
uniform float waveHeight;

mat4 getModelViewProjectionMatrix();
 
void main(void) {
	vec4 v = vec4(gl_Vertex);
	//v.y += sin(0.5 * v.x + waveTime) * waveHeight;
	//TODO: Make the wave motion nicer.
	v.y +=  sin(waveWidth * v.x + waveTime) * cos(waveWidth * v.y + waveTime) * waveHeight;
	gl_Position = getModelViewProjectionMatrix() * v;
}