package com.k1.graphcode.connect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.protocol.HTTP;

import com.k1.graphcode.utils.FileManager;
import com.k1.graphcode.utils.LogUtils;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.MultipartBuilder;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;

public class HttpUtils {

	public static final String IP = "127.0.0.1";
	public static final String PORT = "8080";
	public static final String PATH_DEVICES = "/devices/";
	public static final String PATH_DEVICES_UPDATE = "/devices/update/";
	public static final String PATH_SCRIPTS = "/scripts/";
	public static final String PATH_SCRIPTS_START = "/scripts/start/";
	public static final String PATH_SCRIPTS_STOP = "/scripts/stop/";
	public static final String PATH_SCRIPTS_DELETE = "/scripts/delete/";
	public static final String PATH_SCRIPTS_DOWNLOAD = "/scripts/download/";
	public static final String PATH_SCRIPTS_ADD = "/scripts/add/";

	public static String get(String ip, String port, String path) {
		String uri = "http://" + ip + ":" + port + path;
		BufferedReader in = null;
		try {
//			HttpClient client = new DefaultHttpClient();
//			client.getParams().setParameter(
//					CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
//			String current = uri;
//			HttpGet request = new HttpGet(current);
//			HttpResponse response = client.execute(request);
//			in = new BufferedReader(new InputStreamReader(response.getEntity()
//					.getContent()));
//			StringBuffer sb = new StringBuffer("");
//			String line = "";
//			String NL = System.getProperty("line.separator");
//			while ((line = in.readLine()) != null) {
//				sb.append(line + NL);
//			}
//			in.close();
//			String data = sb.toString();
//			return data;
			return null;
		} catch (Exception e) {
			// e.printStackTrace();
			return e.toString();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

	public static void download(String ip, String port, String path,
			final String name, final LoadingCallback cb) {
		String uri = "http://" + ip + ":" + port + path + name;
		final String finalUri = uri;
		new Thread() {
			public void run() {
				String file = FileManager.getXmlPath() + "/" + name;
//				HttpGet httpGet = new HttpGet(finalUri);
//				try {
//					HttpResponse httpResponse = new DefaultHttpClient()
//							.execute(httpGet);
//					if (httpResponse.getStatusLine().getStatusCode() == 200) {
//						InputStream is = httpResponse.getEntity().getContent();
//						long size = httpResponse.getEntity().getContentLength();
//						FileOutputStream fos = new FileOutputStream(file);
//						byte[] buffer = new byte[8192];
//						int count = 0;
//						int current = 0;
//						while ((count = is.read(buffer)) != -1) {
//							fos.write(buffer, 0, count);
//							current += count;
//							double progress = current * 100 / (double) size;
//							if (cb != null) {
//								cb.progress((int) progress);
//							}
//						}
//						fos.close();
//						is.close();
//						if (cb != null) {
//							cb.finish();
//						}
//					}
//				} catch (Exception e) {
					if (cb != null) {
						cb.error();
					}
//				}
			}
		}.start();
	}

	public static void upload(String ip, String port, String path,
			final String name, final LoadingCallback cb) {
		String uri = "http://" + ip + ":" + port + path;
		final String finalUri = uri;
		new Thread() {
//			public void run() {
//				try {
//					File file = new File(FileManager.getXmlPath() + "/" + name
//							+ ".xml");
//					RequestBody requestBody = new MultipartBuilder()
//							.type(MultipartBuilder.FORM)
//							.addFormDataPart(
//									"file",
//									name + ".xml",
//									RequestBody.create(
//											MediaType.parse("application/xml"),
//											file)).build();
//					Request request = new Request.Builder().url(finalUri)
//							.post(requestBody).build();
//					OkHttpClient client = new OkHttpClient();
//					Response response = client.newCall(request).execute();
//					if (response.isSuccessful()) {
//						if (cb != null) {
//							cb.finish();
//						}
//					} else {
//						if (cb != null) {
//							cb.error();
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}.start();
	}


	public static void post(String ip, String port, String path,
			final String name, final RequestCallback cb) {
		final String uri = "http://" + ip + ":" + port + path;
		LogUtils.d("post : "+ uri);
		new Thread() {
			public void run() {
//				try {
//					HttpClient client = new DefaultHttpClient();
//					client.getParams().setParameter(
//							CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
//					client.getParams().setParameter(
//							CoreConnectionPNames.SO_TIMEOUT, 1000);
//					StringBuilder builder = new StringBuilder();
//					HttpPost post = new HttpPost(uri);
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					params.add(new BasicNameValuePair("script", name));
//					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//					HttpResponse response = client.execute(post);
//					HttpEntity entity = response.getEntity();
//					BufferedReader reader = new BufferedReader(
//							new InputStreamReader(entity.getContent()));
//					for (String s = reader.readLine(); s != null; s = reader
//							.readLine()) {
//						builder.append(s);
//					}
//					cb.callback(null);
//				} catch (Exception e) {
//					if (cb != null) {
//						cb.error(e.toString());
//					}
//				}
			}
		}.start();
	}

	public static void update(String ip, String port, String path,
			final String p, final String value, final RequestCallback cb) {
		final String uri = "http://" + ip + ":" + port + path;
		new Thread() {
			public void run() {
//				try {
//					HttpClient client = new DefaultHttpClient();
//					client.getParams().setParameter(
//							CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
//					client.getParams().setParameter(
//							CoreConnectionPNames.SO_TIMEOUT, 1000);
//					StringBuilder builder = new StringBuilder();
//					HttpPost post = new HttpPost(uri);
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					params.add(new BasicNameValuePair("port", p));
//					params.add(new BasicNameValuePair("value", value));
//					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//					HttpResponse response = client.execute(post);
//					HttpEntity entity = response.getEntity();
//					BufferedReader reader = new BufferedReader(
//							new InputStreamReader(entity.getContent()));
//					for (String s = reader.readLine(); s != null; s = reader
//							.readLine()) {
//						builder.append(s);
//					}
//					if (cb != null) {
//						cb.callback(null);
//					}
//				} catch (Exception e) {
//					if (cb != null) {
//						cb.error(e.toString());
//					}
//				}
			}
		}.start();
	}

	public static final boolean ping(String ip) {
		String result = null;
		try {
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping3次
			// 读取ping的内容，可不加。
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			// PING的状态
			int status = p.waitFor();
			if (status == 0) {
				result = "successful~";
				return true;
			} else {
				result = "failed~ cannot reach the IP address";
			}
		} catch (IOException e) {
			result = "failed~ IOException";
		} catch (InterruptedException e) {
			result = "failed~ InterruptedException";
		} finally {
		}
		LogUtils.d("result :" + result);
		return false;

	}

}