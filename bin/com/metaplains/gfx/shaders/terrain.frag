#version 120

uniform bool isDaytime;
uniform float terrainHeight;

uniform sampler2D texture0;
uniform sampler2D texture1;

uniform float tex0Scale;
uniform float tex1Scale;

varying vec4 worldCoords;

vec4 waterCol = vec4(0.5, 0.5, 0.0, 1.0);
vec4 sandCol = vec4(1.0, 1.0, 0.0, 1.0);
vec4 grassCol = vec4(0.0, 1.0, 0.0, 1.0);
vec4 forestCol = vec4(0.0, 0.5, 0.0, 1.0);
vec4 mountainCol = vec4(0.3, 0.25, 0.25, 1.0);

float getDiffuse();
//float getShadow();

/*float lerp(float x0, float x1, float mu){
	return x0+(x1-x0)*mu;
}*/

float[2] calculateAlpha() {
	if (worldCoords.y < 0.245*64.0) {
		/* Sand */ 
		return float[](1.0, 0.0);
	} else if (worldCoords.y < 0.255*64.0) {
		/* Sand-Grass */
		float fade = ((worldCoords.y/64.0)-0.245)*100.0;
		return float[](1.0-fade, fade);
	} else {
		/* Grass */
		return float[](0.0, 1.0);
	}

	//return vec2((64.0 - worldCoords.y)/64.0, worldCoords.y/64.0);
}

vec4 calculateColor() {
	if (worldCoords.y < 0.2*64.0) { //TODO: Scale as an attribute (check Water too).
		/* Water */
		return waterCol;
	} else if (worldCoords.y < 0.25*64.0) {
		/* Sand */
		return sandCol;
	} else if (worldCoords.y < 0.4*64.0) {
		/* Grass */
		return grassCol;
	} else if (worldCoords.y < 0.5*64.0) {
		/*Forest*/
		return forestCol;
	} else {
		/* Mountain */
		return mountainCol;
	}
}

void main() {
	if (worldCoords.y > 0.008 * 64.0) {
		float[2] alpha = calculateAlpha();
		
		gl_FragColor = vec4(((alpha[0]*texture2D(texture0,  worldCoords.xz/tex0Scale/*(gl_TexCoord[0].st)/tex0Scale*/)
						+ alpha[1]*texture2D(texture1,  worldCoords.xz/tex1Scale/*(gl_TexCoord[0].st)/tex1Scale*/)) /** calculateColor()*/
						* (isDaytime?getDiffuse():0.1) /* getShadow()*/).rgb, 1.0);
	} else {
		gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
	}
}