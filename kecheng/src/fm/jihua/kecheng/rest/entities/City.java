package fm.jihua.kecheng.rest.entities;

import com.ngohung.widget.ContactItemInterface;

public class City implements ContactItemInterface {
	public int id;
	public String name;
	public String pinyin;
	public String py;
	
	public City(int id, String name, String pinyin, String py){
		this.id = id;
		this.name = name;
		this.pinyin = pinyin;
		this.py = py;
	}

	@Override
	public String getItemForIndex() {
		return this.py;
	}
	
	@Override
	public String getItemForName(){
		return this.name;
	}
}
