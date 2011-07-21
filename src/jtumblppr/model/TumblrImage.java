package jtumblppr.model;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable
public class TumblrImage {
	
    @PrimaryKey
    private String id;
    
	@Persistent
    private Blob image;
	
	@Persistent
	private Date date;
	
	@Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
    private String imageType;
	
	public TumblrImage() {}
	public TumblrImage(String id, byte[] image, String imageType) {
		this.id = id;
		setImage(image);
		this.imageType = imageType;
		this.date = new Date();
	}
	
	public void setImage(byte[] bytes) {
        this.image = new Blob(bytes);
    }
	
	public byte[] getImage() {
        if (image == null) {
            return null;
        }

        return image.getBytes();
    }
	
	public String getId() {
		return this.id;
	}
	
	public Date getDate() {
		return this.date;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getImageType() {
		return imageType;
	}
	
	
}
