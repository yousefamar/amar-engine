package com.metaplains.gfx;

import static org.lwjgl.opengl.GL11.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;

/**
 * A class that can load, edit and store .png files.
 * Model skins are implicitly loaded.
 * @author Paraknight
 */
public class Texture {

	public static int NONE;
	
	public static int NOSIGNAL;
	public static int SKYBOX;
	public static int TREE;
	public static int BARREL;
	
	public static int TERRAIN;
	public static int GRASSDARK;
	public static int TEMPSAND;
	
	public static int RADIO;
	
	public static void loadTextures() {
		BufferedImage noneImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		noneImg.setRGB(0, 0, 0xFFFFFFFF);
		NONE = loadTexture(noneImg);
		
		NOSIGNAL = loadTexture(loadImage("nosignal.png"));
		SKYBOX = loadTexture(loadImage("skybox.png"));
		
		BufferedImage textureMap = loadImage("spritemap.png");
		
		TREE = loadTexture(loadImage("tree.png"));
		BARREL = loadTexture(textureMap.getSubimage(64, 0, 32, 32));
		
		TERRAIN = loadTextureMipmaps(textureMap.getSubimage(96, 0, 32, 32));
		GRASSDARK = loadTextureMipmaps(loadImage("grassdark.png"));
		TEMPSAND = loadTextureMipmaps(loadImage("tempsand.png"));
		
		RADIO = loadTexture(loadImage("radio.png"));
	}

	public static BufferedImage loadImage(String fileName) {
		//TODO: Make a no texture found texture;
		BufferedImage img = null;
		try {
			img = ImageIO.read(Texture.class.getResource("textures/"+fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static int loadTexture(BufferedImage img) {
		int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

        ByteBuffer imgBuf = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
        
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                int pixel = pixels[y * img.getWidth() + x];
                imgBuf.put((byte) ((pixel >> 16) & 0xFF));	//R
                imgBuf.put((byte) ((pixel >> 8) & 0xFF));	//G
                imgBuf.put((byte) (pixel & 0xFF));			//B
                imgBuf.put((byte) ((pixel >> 24) & 0xFF));	//A
            }
        }
        imgBuf.flip();

		IntBuffer intBuf = BufferUtils.createIntBuffer(1);
		glGenTextures(intBuf);
		intBuf.rewind();
		int texID = intBuf.get(0);
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imgBuf);
		return texID;
	}
	
	public static int loadTextureMipmaps(BufferedImage img) {
		int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

        ByteBuffer imgBuf = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
        
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                int pixel = pixels[y * img.getWidth() + x];
                imgBuf.put((byte) ((pixel >> 16) & 0xFF));	//R
                imgBuf.put((byte) ((pixel >> 8) & 0xFF));	//G
                imgBuf.put((byte) (pixel & 0xFF));			//B
                imgBuf.put((byte) ((pixel >> 24) & 0xFF));	//A
            }
        }
        imgBuf.flip();

		IntBuffer intBuf = BufferUtils.createIntBuffer(1);
		glGenTextures(intBuf);
		intBuf.rewind();
		int texID = intBuf.get(0);
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		GLU.gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGBA, img.getWidth(), img.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, imgBuf);
		return texID;
	}
}
