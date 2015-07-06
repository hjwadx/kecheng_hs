package fm.jihua.kecheng.rest.entities;

public class UsersResult extends BaseResult {
	public User[] users;
	public String[] invite_third_part_ids;
	public int current_page;
	public int total_pages;
}
