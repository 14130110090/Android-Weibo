package com.weibo.connect;

import static com.weibo.utils.ConstantUtil.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.adapter.AsyncDrawable;
import com.weibo.adapter.BitmapLoadTask;
import com.weibo.utils.FileLruCache;
import com.weibo.utils.MemoryLruCache;
import com.weibo.utils.PictureUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

public class ConnectManager {
	GetPhotoListListener listlistener;

	ConnectToServer connect = null;
	// 单次最大传输的字符串长度
	int MAXLENGTH = 20000;
	OnJsonReturnListener jsonListener;

	public ConnectManager(GetPhotoListListener listener) {
		this.listlistener = listener;
	}

	public ConnectManager() {

	}

	public ConnectManager(OnJsonReturnListener listener) {
		jsonListener = listener;
	}

	public interface OnJsonReturnListener {
		public void onJsonReturn(JSONObject result);
	}

	public void setOnJsonReturnListener(OnJsonReturnListener listener) {
		jsonListener = listener;
	}

	public void getDiaryList(final int user_id){
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", DIARYLIST);
					json.put("userid", user_id);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	public void getUserInfo(final int user_id,final int visitor_id){
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", USERINFO);
					json.put("user_id", user_id);
					json.put("visitor_id", visitor_id);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void addLaud(final int user_id, final int diary_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", ADDLAUD);
						json.put("user_id", user_id);
						json.put("diary_id", diary_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}
	public void deleteDiary(final int diary_id){
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", DELETEDIARY);
					json.put("diaryid", diary_id);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void uploadComment(final int user_id, final int diary_id,
			final String comment) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", SAVECOMMENT);
						json.put("user_id", user_id);
						json.put("diary_id", diary_id);
						json.put("comment_content", comment);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}

	public void getComments(final int diary_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", GETCOMMENT);
					json.put("diary_id", diary_id);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}

	public void getPhotoFromAlbum(final int albumid) {
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", ALBUM);
						json.put("albumid", albumid);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (result.getString("action").equals(ALBUM_SUCCESS)) {
						if (listlistener != null) {
							listlistener.setPhotoList(result);
						}

						// 判断是否有消息
						// if (info.length() != 0) {
						// String[] items = info.split("#");
						//
						// byte[][] picture;
						// int length = din.readInt();
						// picture = new byte[length][];
						// for (int i = 0; i < length; i++) {
						// byte[] buffer;
						// String str = din.readUTF();
						// buffer = Base64.decode(str, Base64.DEFAULT);
						// for (int y = 0; y < buffer.length; y++) {
						// if (buffer[y] < 0) {
						// buffer[y] += 256;
						// }
						// }
						// picture[i] = buffer;
						// }
						// listlistener.setPhotoList(items, picture);
						// } else {
						// listlistener.setPhotoList(null, null);
					} else if (result.getString("action").equals(ALBUM_FAIL)) {
						// 不可以在非主线程直接使用Toast
						// Toast.makeText(LoginActivity.this, "登录失败",
						// Toast.LENGTH_SHORT).show();
						System.out.println("获取照片失败");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}

	public void getPhotoOfUser(final int user_id) {
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", ALBUM);
						json.put("user_id", user_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}

	public void register(final String account,final String password) {
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", REGISTER);
						json.put("account", account);
						json.put("password", password);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}
	
	public boolean deletePhoto(final int picid, final int albumid) {
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					JSONObject json = new JSONObject();
					json.put("action", DELETEPHOTO);
					json.put("picid", picid);
					connect.getOutputStream().writeUTF(json.toString());
					String receivedMessage = connect.getInputStream().readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (result.getString("action").equals(DELETEPHOTO_SUCCESS)) {
						getPhotoFromAlbum(albumid);
					} else {
						// 不可以在非主线程直接使用Toast
						// Toast.makeText(LoginActivity.this, "登录失败",
						// Toast.LENGTH_SHORT).show();
						System.out.println(DELETEPHOTO_FAIL);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

		return true;
	}

	// 舍弃
	public boolean getPhoto(final int picid, final int id) {
		Thread t = new Thread() {
			public void run() {
				// 若要使用Toast
				// Looper.prepare();
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}

				try {
					String msg = GETPHOTO + picid;
					connect.getOutputStream().writeUTF(msg);
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					if (receivedMessage.startsWith(GETPHOTO_SUCCESS)) {

						String info = receivedMessage
								.substring(GETPHOTO_SUCCESS.length());
						// 判断是否有消息
						if (info.length() != 0) {
							// 图片信息先不管
							String[] items = info.split("#");
							byte[] picture;
							String str = din.readUTF();
							picture = Base64.decode(str, Base64.DEFAULT);
							for (int y = 0; y < picture.length; y++) {
								if (picture[y] < 0) {
									picture[y] += 256;
								}
							}
							// if (listener != null) {
							// listener.setPhoto(picture, id);
							// }
						} else {
							System.out.println("无图片");
						}

					} else if (receivedMessage.startsWith(GETPHOTO_FAIL)) {
						// 不可以在非主线程直接使用Toast
						// Toast.makeText(LoginActivity.this, "登录失败",
						// Toast.LENGTH_SHORT).show();
						System.out.println("fail");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

		return true;
	}

	public void setAttention(final int user_id, final int concerned_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", SETATTENTION);
						json.put("user_id", user_id);
						json.put("concerned_id", concerned_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void alterUserInfo(final int user_id, final String user_name,final String user_desc,final String user_password,final String user_email) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", ALTERUSERINFO);
						json.put("user_id", user_id);
						json.put("user_name", user_name);
						json.put("user_desc", user_desc);
						json.put("user_email", user_email);
						json.put("user_password", user_password);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void addTransmit(final int user_id, final int diary_id,
			final String transmit) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", ADDTRANSMIT);
						json.put("user_id", user_id);
						json.put("diary_id", diary_id);
						json.put("transmit_content", transmit);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();
	}

	public void cancelAttention(final int user_id, final int concerned_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", CANCELATTENTION);
						json.put("user_id", user_id);
						json.put("concerned_id", concerned_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	// 获取所有关注者
	public void getAttention(final int user_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", GETATTENTION);
						json.put("user_id", user_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void getFans(final int user_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", GETFANS);
						json.put("user_id", user_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void searchUser(final int user_id,final String user_name) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", SEARCHUSER);
						json.put("user_id", user_id);
						json.put("user_name", user_name);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}
	
	public void setCollection(final int user_id, final int diary_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", SETCOLLECTION);
						json.put("user_id", user_id);
						json.put("diary_id", diary_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void cancelCollection(final int user_id, final int diary_id) {
		Thread t = new Thread() {
			public void run() {
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						System.out.println("weilianjie");
						connect = null;
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					try {
						json.put("action", CANCELCOLLECTION);
						json.put("user_id", user_id);
						json.put("diary_id", diary_id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					connect.getOutputStream().writeUTF(json.toString());
					DataInputStream din = connect.getInputStream();
					String receivedMessage = din.readUTF();
					JSONObject result = new JSONObject(receivedMessage);
					if (jsonListener != null) {
						jsonListener.onJsonReturn(result);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		t.start();

	}

	public void uploadHead(final int user_id, final String path,
			final String description, final int album_id) {
		Thread thread = new Thread() {
			public void run() {
				// 如果没有连接就创建连接
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						// 要将这个信息提示给用户，不是打印
						System.out.println("weilianjie");
						connect = null;// 重设为null，否则下次点击就会跳过这里也就再也不会重新连接
						return;
					}
				}
				try {
					JSONObject json = new JSONObject();
					String encoder = Base64
							.encodeToString(PictureUtil.getBytes(path), Base64.NO_WRAP);
					try {
						json.put("action", UPLOAD);
						json.put("user_id", user_id);
						json.put("album_id", album_id);
						json.put("description", description);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					DataOutputStream output = connect.getOutputStream();
					output.writeUTF(json.toString());

					// 代表一共传输几次
					int count = encoder.length() / MAXLENGTH + 1;
					// 判断是否相等，决定要不要加一
					if (count * MAXLENGTH == encoder.length())
						count--;
					output.writeInt(count);
					for (int m = 0; m < count; m++) {
						if (m == count - 1) {
							output.writeUTF(encoder.substring(m * (MAXLENGTH)));
							break;
						}
						output.writeUTF(encoder.substring(m * MAXLENGTH,
								(m + 1) * MAXLENGTH));
					}

					output.flush();
					String receivedMessage = connect.getInputStream().readUTF();
					if (receivedMessage.startsWith(UPLOAD_SUCCESS)) {
						System.out.println("上传成功");

						// 停止本Activity运行

					} else if (receivedMessage.startsWith(UPLOAD_FAIL)) {
						// 不可以在非主线程直接使用Toast
						// Toast.makeText(LoginActivity.this, "登录失败",
						// Toast.LENGTH_SHORT).show();
						System.out.println("fail");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (connect != null) {
					connect.closeConnection();
					connect = null;
				}
			}
		};
		thread.start();
	}

	/**
	 * 解决listview中重用currentView和异步加载带来的问题，因为重用view，某些item共用
	 * 同一块内存，一个item使用其数据刷新view时，其他item其实界面也改变了，只不过看不见，
	 * 等到滑动到其他item时，其他item再调用其数据刷新view（所以如果这时item不刷新界面，显示
	 * 的就是先前item的界面）。之所以会发生图片闪烁，因为同一个view的各个item都在刷新界面。
	 * 解决办法：先判断有没有其他item正在加载资源，没有就直接加载，有的话 再判断别的item加载的资源路径是否和当前要加载的一致，一致就不重新加载。
	 * 
	 * @param resPath
	 *            ,非绝对地址，只传后面的相对地址
	 * @param imageView
	 */
	public static void loadBitmap(MemoryLruCache mLruCache,
			FileLruCache fileCache, String resPath, ImageView imageView) {
		Bitmap bitmap = null;
		if (mLruCache != null) {
			bitmap = mLruCache.getBitmapFromCache(resPath);
		}
		if (bitmap == null) {
			if (fileCache != null) {
				bitmap = fileCache.getBitmap(resPath);
				if (bitmap != null) {
					Log.i("myinfo", "从sd卡获取的bitmap");
					if (mLruCache != null)
						mLruCache.addBitmapToCache(resPath, bitmap);
					imageView.setImageBitmap(bitmap);
					return;
				}
			}
			Log.i("myinfo", "重新获取新的bitmap");
			if (cancelPotentialWork(resPath, imageView)) {
				final BitmapLoadTask task = new BitmapLoadTask(imageView,
						resPath, mLruCache, fileCache);
				// 不需要在这里设置背景，直接在xml的background中设置就好了
				final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute();
			}
		} else {
			Log.i("myinfo", "从缓存中直接获取的bitmap");
			imageView.setImageBitmap(bitmap);
		}
	}

	// 判断是否有线程正在为该imageview下载图片
	// 如果有任务现在正在下载，那么判断下载的这个图片资源（url）是否和现在的图片资源一样，
	// 不一样则取消之前的线程（之前的下载线程作废）

	// 首先检查当前是否已经有一个AsyncTask正在为这个ImageView加载图片，如果没有就直接返回true。
	// 如果有，再检查这个Task正在加载的资源是否与自己正要进行加载的资源相同，
	// 如果相同，那就没有必要再进行多一次的加载了，直接返回false；
	// 而如果不同（为什么会不同？文章最后会有解释），就取消掉这个正在进行的任务，并返回true。
	public static boolean cancelPotentialWork(String Path, ImageView imageView) {
		final BitmapLoadTask bitmapWorkerTask = getBitmapLoadTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapPath = bitmapWorkerTask.getPath();
			if (!bitmapPath.equals(Path)) {
				// 取消之前的任务
				bitmapWorkerTask.cancel(true);
			} else {
				// 相同任务已经存在，直接返回false，不再进行重复的加载
				return false;
			}
		}
		// 没有Task和ImageView进行绑定，或者Task由于加载资源不同而被取消，返回true
		return true;
	}

	/**
	 * 获取ImageView所对应的Bitmap的BitmapLoadTask
	 * 
	 * @param imageView
	 * @return
	 */
	public static BitmapLoadTask getBitmapLoadTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	public interface GetPhotoListListener {
		public void setPhotoList(JSONObject items);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (reqWidth == 0 || reqHeight == 0) {
			return inSampleSize;
		}
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最大的比率作为inSampleSize的值，这样可以保证insamplesize大于1
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}
		return inSampleSize;
	}

	public static Bitmap readBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {
		// inJustDecodeBounds：如果设置为true，将不把图片的像素数组加载到内存中，仅加载一些额外的数据到Options中。
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap readBitmapFromFile(String path, int reqWidth,
			int reqHeight) {
		// inJustDecodeBounds：如果设置为true，将不把图片的像素数组加载到内存中，仅加载一些额外的数据到Options中。
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap readBitmapFromInputStream(InputStream stream,
			int reqWidth, int reqHeight) {
		// inJustDecodeBounds：如果设置为true，将不把图片的像素数组加载到内存中，仅加载一些额外的数据到Options中。
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		byte[] data = new byte[0];
		try {
			data = getByteArrayFromStream(stream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;

		// 之前已经读取过一次InputStream流了，
		// 所以再次通过InputStream流得到bitmap的时候就为空了，
		// 因为InputStream流的指针已经到了结尾，下次读取就为空了。

		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	// 从流中获取byte数组
	public static byte[] getByteArrayFromStream(InputStream inStream)
			throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

}
