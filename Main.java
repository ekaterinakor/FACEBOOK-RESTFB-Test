package facebooktest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchRequest.BatchRequestBuilder;
import com.restfb.batch.BatchResponse;
//import com.restfb.batch.BatchRequest;
import com.restfb.experimental.api.Posts;
import com.restfb.experimental.api.Users;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.scope.ScopeBuilder;
import com.restfb.scope.UserDataPermissions;
import com.restfb.types.DeviceCode;
import com.restfb.types.Post;
import com.restfb.types.User;


public class Main
{
	static Version v=Version.VERSION_1_0;

	// это приложение выводит на экран содержимое параметра code для удобного копирования в буфер 
	// http://my-test-project-147319.appspot.com
	static String main_app_link="http://rpserver.dlinkddns.com:81/code.php";
	static String token_id="EAAWvRhAgZABYBAHGTrGL0XEIAkvIRc0AMz3QTC2cKoVIdqDJtc0FgB6hrwly0pizQgo1kAiIx3gzXuiCYToDVnUzS3N2eYpvavZCurBQCK2DxhSZCD1xSe1PLgDZBIsG4dmswt0P0DBe9gi1vOZAxRUMWTRDD4GwZD";
	

	public static void main(String[] args)
	{
		FacebookClient client=null;
	
/////// проверим, можем ли мы с текузим token_id работать?
		try
		{
			client = new DefaultFacebookClient(token_id, v);
			User user = client.fetchObject("me", User.class);
			System.out.println(user.getName());
		}
/////// если не смогли, то значит проводим операцию по запросу нового токена
/////// выводим ссылку для браузера, по пользователь по ней переходит, вводит логинпароль, подтверждает выдачу прав приложению
		catch(Exception e)
		{
			System.out.println("Need reauth? "+e.getMessage());

			try
			{
/////// набираем права, требуемые приложению
				ScopeBuilder scopeBuilder = new ScopeBuilder();
				scopeBuilder.addPermission(UserDataPermissions.USER_POSTS);
				scopeBuilder.addPermission(UserDataPermissions.USER_FRIENDS);
				scopeBuilder.addPermission(UserDataPermissions.USER_ABOUT_ME);			
				client = new DefaultFacebookClient(v);
/////// генерируем и выводим ссылку  диалога авторизации
				String loginDialogUrlString = client.getLoginDialogUrl("1600090336683030", main_app_link, scopeBuilder);		
				System.out.println(loginDialogUrlString);
				
	//			DeviceCode deviceCode = client.fetchDeviceCode("1600090336683030", scopeBuilder);
	//			System.out.println(deviceCode.getVerificationUri());
	//			System.out.println("Enter this Code: " + deviceCode.getUserCode());
/////// ожидаем от пользователя код, который вернуло нам приложение
				byte[] buf=new byte[10240];
				int cnt=System.in.read(buf, 0, 10240);
				String code=new String(buf,0,cnt).trim();
				System.out.println("My access code: '"+code+"'");
				
	//			String deviceCodes=deviceCode.getCode();
	//			System.out.println("My device coded: " + deviceCode);
/////// используя это ткод запрашиваем новый временный токен
	//			AccessToken accessToken = client.obtainDeviceAccessToken("1600090336683030", deviceCodes);
				AccessToken accessToken = client.obtainUserAccessToken("1600090336683030", "38c6cf48b3326bb7f644263ab21d7348", main_app_link, code);
				System.out.println("My access token: " + accessToken.getAccessToken());
/////// повторно создаем инстанс FacebookClient уже на сонове полученного токена				
				client = new DefaultFacebookClient(accessToken.getAccessToken(), v);
/////// повторно запрашиваем информацию (это не обязательно, просто чтобы вывод был одинаков для обоих случаев)
				User user = client.fetchObject("me", User.class);
				System.out.println(user.getFirstName());
			}
			catch ( Exception e2)
			{
				e2.printStackTrace();
				System.exit(0);
			}
		}
		User user = client.fetchObject("me", User.class);
		Connection<Post> myFeed = client.fetchConnection("/"+user.getId()+"/feed", Post.class);
		Connection<User> myFriends = client.fetchConnection("/me/friendlists", User.class);
		
		JsonObject fj=client.fetchObject("me/friends", JsonObject.class);
		System.out.println("Count of my friends: " +myFriends.getTotalCount());
		while (myFriends.hasNext())
		{
		    for (List<User> friends : myFriends) {
		    	  for (User fr : friends) {
		    	    System.out.println(fr.getId());
		    	    //Connection<User> user1 = client.fetchConnection(fr.getId(), User.class);
		    	   // System.out.println(user1);
		    	  }
		    	} 
		    	
			System.out.println("dfd");
			// myFriends.iterator().next();
		}
		for (Iterator i = myFriends.iterator(); i.hasNext(); )
			{
			List<User> u= (List<User>) i.next();
			//System.out.println(myFriends.getAfterCursor());
			}
		//BatchRequest postRequest = new BatchRequestBuilder("me/feed") 
		

	   // System.out.println("Count of my friends: " + fj.);
//	    for (List<Post> myFeedConnectionPage : myFeed)
//	    	  for (Post post : myFeedConnectionPage)
//	    		  System.out.println("Post: " + post);
	    //List<User> fj=myFriends.getData();
	    
	    
	    //System.out.println(fj.optJsonArray("data"));
	    //if (fj.has(key))
	   // System.out.println(fj.getJsonArray("data").g.getJsonObject(0).getString("name"));
        String after="";
		JsonArray data=fj.getJsonArray("data");
		System.out.println(data.getJsonObject(0).getString("name"));
		if(fj.has("paging")) {
//			System.out.println("dfgfd");
			JsonObject jsonPaging = fj.getJsonObject("paging");
			if(jsonPaging != null) {
//				System.out.println("dfgfd");
				if(jsonPaging.has("cursors")) {
                    JsonObject jsonCursors = jsonPaging.getJsonObject("cursors");
					if(jsonCursors != null && jsonCursors.has("after"))
                        after = jsonCursors.getString("after");
					System.out.println(after);
                }
				List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
				//BatchRequest request = new BatchRequestBuilder("/me/friends?limit=100&after="+after).build();
				BatchRequest request = new BatchRequestBuilder("/me/friendlists").build();
				batchRequests.add(request);
				List<BatchResponse> res = client.executeBatch(batchRequests);
				System.out.println(res.get(0));
//				BatchRequests<BatchRequest> batch = new BatchRequests<BatchRequest>();
//				batch.add(new BatchRequest(RequestMethod.GET, "mefriends?after=" + after));
//				Object resultsPhotos = client.executeBatch(batch).get(0);
//				boolean hasNext;
//				if(jsonPaging.has("next")) hasNext = true;
			}
		}
//	    for (int i=1;i<fj.length();i++)
//	    {
//	    	String firstPhotoUrl = fj.getJsonObject(Integer.toString(i)).getString("data");
//	    	System.out.println(firstPhotoUrl);
//	    }
	    
	    
//		BatchRequest batch = new BatchRequest(RequestMethod.GET, null, main_app_link, null, null, main_app_link, main_app_link, main_app_link, false);
	    for (List<User> friends : myFriends) {
	    	  for (User fr : friends) {
	    	    System.out.println(fr.getId());
	    	    //Connection<User> user1 = client.fetchConnection(fr.getId(), User.class);
	    	   // System.out.println(user1);
	    	  }
	    	} 
	    	
	    //for (User myFriendsList : fj)
//	    	  for (User user1 : myFriendsList )
	    		 // System.out.println("User: " + myFriendsList.getFirstName());

	}

}
