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
	// �����������ַ�������
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
				// ��Ҫʹ��Toast
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
				// ��Ҫʹ��Toast
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
				// ��Ҫʹ��Toast
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
				// ��Ҫʹ��Toast
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

						// �ж��Ƿ�����Ϣ
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
						// �������ڷ����߳�ֱ��ʹ��Toast
						// Toast.makeText(LoginActivity.this, "��¼ʧ��",
						// Toast.LENGTH_SHORT).show();
						System.out.println("��ȡ��Ƭʧ��");
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
				// ��Ҫʹ��Toast
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
				// ��Ҫʹ��Toast
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
				// ��Ҫʹ��Toast
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
						// �������ڷ����߳�ֱ��ʹ��Toast
						// Toast.makeText(LoginActivity.this, "��¼ʧ��",
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

	// ����
	public boolean getPhoto(final int picid, final int id) {
		Thread t = new Thread() {
			public void run() {
				// ��Ҫʹ��Toast
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
						// �ж��Ƿ�����Ϣ
						if (info.length() != 0) {
							// ͼƬ��Ϣ�Ȳ���
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
							System.out.println("��ͼƬ");
						}

					} else if (receivedMessage.startsWith(GETPHOTO_FAIL)) {
						// �������ڷ����߳�ֱ��ʹ��Toast
						// Toast.makeText(LoginActivity.this, "��¼ʧ��",
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

	// ��ȡ���й�ע��
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
				// ���û�����Ӿʹ�������
				if (connect == null) {
					connect = new ConnectToServer(ADDRESS, PORT);
					if (!connect.isConnected()) {
						// Ҫ�������Ϣ��ʾ���û������Ǵ�ӡ
						System.out.println("weilianjie");
						connect = null;// ����Ϊnull�������´ε���ͻ���������Ҳ����Ҳ������������
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

					// ����һ�����伸��
					int count = encoder.length() / MAXLENGTH + 1;
					// �ж��Ƿ���ȣ�����Ҫ��Ҫ��һ
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
						System.out.println("�ϴ��ɹ�");

						// ֹͣ��Activity����

					} else if (receivedMessage.startsWith(UPLOAD_FAIL)) {
						// �������ڷ����߳�ֱ��ʹ��Toast
						// Toast.makeText(LoginActivity.this, "��¼ʧ��",
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
	 * ���listview������currentView���첽���ش��������⣬��Ϊ����view��ĳЩitem����
	 * ͬһ���ڴ棬һ��itemʹ��������ˢ��viewʱ������item��ʵ����Ҳ�ı��ˣ�ֻ������������
	 * �ȵ�����������itemʱ������item�ٵ���������ˢ��view�����������ʱitem��ˢ�½��棬��ʾ
	 * �ľ�����ǰitem�Ľ��棩��֮���Իᷢ��ͼƬ��˸����Ϊͬһ��view�ĸ���item����ˢ�½��档
	 * ����취�����ж���û������item���ڼ�����Դ��û�о�ֱ�Ӽ��أ��еĻ� ���жϱ��item���ص���Դ·���Ƿ�͵�ǰҪ���ص�һ�£�һ�¾Ͳ����¼��ء�
	 * 
	 * @param resPath
	 *            ,�Ǿ��Ե�ַ��ֻ���������Ե�ַ
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
					Log.i("myinfo", "��sd����ȡ��bitmap");
					if (mLruCache != null)
						mLruCache.addBitmapToCache(resPath, bitmap);
					imageView.setImageBitmap(bitmap);
					return;
				}
			}
			Log.i("myinfo", "���»�ȡ�µ�bitmap");
			if (cancelPotentialWork(resPath, imageView)) {
				final BitmapLoadTask task = new BitmapLoadTask(imageView,
						resPath, mLruCache, fileCache);
				// ����Ҫ���������ñ�����ֱ����xml��background�����þͺ���
				final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute();
			}
		} else {
			Log.i("myinfo", "�ӻ�����ֱ�ӻ�ȡ��bitmap");
			imageView.setImageBitmap(bitmap);
		}
	}

	// �ж��Ƿ����߳�����Ϊ��imageview����ͼƬ
	// ��������������������أ���ô�ж����ص����ͼƬ��Դ��url���Ƿ�����ڵ�ͼƬ��Դһ����
	// ��һ����ȡ��֮ǰ���̣߳�֮ǰ�������߳����ϣ�

	// ���ȼ�鵱ǰ�Ƿ��Ѿ���һ��AsyncTask����Ϊ���ImageView����ͼƬ�����û�о�ֱ�ӷ���true��
	// ����У��ټ�����Task���ڼ��ص���Դ�Ƿ����Լ���Ҫ���м��ص���Դ��ͬ��
	// �����ͬ���Ǿ�û�б�Ҫ�ٽ��ж�һ�εļ����ˣ�ֱ�ӷ���false��
	// �������ͬ��Ϊʲô�᲻ͬ�����������н��ͣ�����ȡ����������ڽ��е����񣬲�����true��
	public static boolean cancelPotentialWork(String Path, ImageView imageView) {
		final BitmapLoadTask bitmapWorkerTask = getBitmapLoadTask(imageView);

		if (bitmapWorkerTask != null) {
			final String bitmapPath = bitmapWorkerTask.getPath();
			if (!bitmapPath.equals(Path)) {
				// ȡ��֮ǰ������
				bitmapWorkerTask.cancel(true);
			} else {
				// ��ͬ�����Ѿ����ڣ�ֱ�ӷ���false�����ٽ����ظ��ļ���
				return false;
			}
		}
		// û��Task��ImageView���а󶨣�����Task���ڼ�����Դ��ͬ����ȡ��������true
		return true;
	}

	/**
	 * ��ȡImageView����Ӧ��Bitmap��BitmapLoadTask
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
		// ԴͼƬ�ĸ߶ȺͿ��
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (reqWidth == 0 || reqHeight == 0) {
			return inSampleSize;
		}
		if (height > reqHeight || width > reqWidth) {
			// �����ʵ�ʿ�ߺ�Ŀ���ߵı���
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// ѡ���͸������ı�����ΪinSampleSize��ֵ���������Ա�֤insamplesize����1
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}
		return inSampleSize;
	}

	public static Bitmap readBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {
		// inJustDecodeBounds���������Ϊtrue��������ͼƬ������������ص��ڴ��У�������һЩ��������ݵ�Options�С�
		// ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// �������涨��ķ�������inSampleSizeֵ
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap readBitmapFromFile(String path, int reqWidth,
			int reqHeight) {
		// inJustDecodeBounds���������Ϊtrue��������ͼƬ������������ص��ڴ��У�������һЩ��������ݵ�Options�С�
		// ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// �������涨��ķ�������inSampleSizeֵ
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap readBitmapFromInputStream(InputStream stream,
			int reqWidth, int reqHeight) {
		// inJustDecodeBounds���������Ϊtrue��������ͼƬ������������ص��ڴ��У�������һЩ��������ݵ�Options�С�
		// ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		byte[] data = new byte[0];
		try {
			data = getByteArrayFromStream(stream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// �������涨��ķ�������inSampleSizeֵ
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ
		options.inJustDecodeBounds = false;

		// ֮ǰ�Ѿ���ȡ��һ��InputStream���ˣ�
		// �����ٴ�ͨ��InputStream���õ�bitmap��ʱ���Ϊ���ˣ�
		// ��ΪInputStream����ָ���Ѿ����˽�β���´ζ�ȡ��Ϊ���ˡ�

		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	// �����л�ȡbyte����
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
