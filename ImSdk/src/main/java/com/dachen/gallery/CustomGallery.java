package com.dachen.gallery;

import java.io.Serializable;

public class CustomGallery implements Serializable{

	public String sdcardPath;
//	public boolean isSeleted = false;
//	public boolean isGoCamera = false;

	public CustomGallery( String sdcardPath) {
		this.sdcardPath = sdcardPath;
	}
//	public CustomGallery(boolean isGoCamera, String sdcardPath) {
//		this.isGoCamera = isGoCamera;
//		this.sdcardPath = sdcardPath;
//	}
}
