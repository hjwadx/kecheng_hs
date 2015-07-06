package fm.jihua.kecheng.rest.entities;

public class AvatarOrFile {
	
	public Avatar avatar;
	public String fileName;
	public String path;
	
	public AvatarOrFile (Avatar avatar) {
		this.avatar = avatar;
	}
	
	public AvatarOrFile (String name, String path) {
		this.fileName = name;
		this.path = path;
	}
	
	public boolean isAvatar () {
		return avatar != null;	
	}
	
	public boolean isEmpty () {
		return avatar == null && fileName == null;	
	}

}
