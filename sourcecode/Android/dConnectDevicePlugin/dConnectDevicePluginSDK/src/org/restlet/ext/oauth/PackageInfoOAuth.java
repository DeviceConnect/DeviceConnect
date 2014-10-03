package org.restlet.ext.oauth;

/**
 * OAuthアクセストークンを発行する相手を識別するデータ.<br>
 * - アプリ(Android)の場合は、パッケージ名を入れる。<br>
 * - アプリ(Web)の場合は、パッケージ名にURLを入れる。<br>
 * - デバイスプラグインの場合は、パッケージ名とデバイスIDを入れる。<br>
 */
public class PackageInfoOAuth {
	
	/** パッケージ名. */
	private String mPackageName;
	
	/** デバイスID(アプリの場合はnullを設定する). */
	private String mDeviceId;
	
	/**
	 * コンストラクタ(アプリを指定する場合).
	 * @param packageName	パッケージ名.
	 */
	public PackageInfoOAuth(final String packageName) {
		mPackageName = packageName;
		mDeviceId = null;
	}
	
	/**
	 * コンストラクタ(デバイスプラグインを指定する場合).
	 * @param packageName	パッケージ名.
	 * @param deviceId		デバイスID.
	 */
	public PackageInfoOAuth(final String packageName, final String deviceId) {
		mPackageName = packageName;
		mDeviceId = deviceId;
	}
	
	/**
	 * パッケージ名取得.
	 * @return	パッケージ名
	 */
	public String getPackageName() {
		return mPackageName;
	}
	
	/**
	 * デバイスID取得.
	 * @return	デバイスID
	 */
	public String getDeviceId() {
		return mDeviceId;
	}
	
	/**
	 * オブジェクト比較.
	 * @param o	比較対象のオブジェクト
	 * @return true: 同じ値を持つオブジェクトである。 / false: 異なる値を持っている。
	 */
	@Override
	public boolean equals(final Object o) {
		
		PackageInfoOAuth cmp1 = this;
		PackageInfoOAuth cmp2 = (PackageInfoOAuth) o;
		
		boolean isEqualPackageName = false;
		if (cmp1.getPackageName() == null && cmp2.getPackageName() == null) {		/* 両方null */
			isEqualPackageName = true;
		} else if (cmp1.getPackageName() != null && cmp2.getPackageName() != null 	/* 両方同じ文字列 */
				&& cmp1.getPackageName().equals(cmp2.getPackageName())) {
			isEqualPackageName = true;
		}
		
		boolean isEqualDeviceId = false;
		if (cmp1.getDeviceId() == null && cmp2.getDeviceId() == null) {				/* 両方null */
			isEqualDeviceId = true;
		} else if (cmp1.getDeviceId() != null && cmp2.getDeviceId() != null 		/* 両方同じ文字列 */
				&& cmp1.getDeviceId().equals(cmp2.getDeviceId())) {
			isEqualDeviceId = true;
		}
		
		if (isEqualPackageName && isEqualDeviceId) {
			return true;
		}
		return false;
	}
	
	/**
	 * ハッシュ値を返す.
	 * @return	ハッシュ値 
	 */
	@Override
	public int hashCode() {
		
		String str = "";
		if (getPackageName() != null) {
			str += getPackageName();
		} else {
			str += "(null)";
		}
		if (getDeviceId() != null) {
			str += getDeviceId();
		} else {
			str += "(null)";
		}
		
		int hashCode = str.hashCode();
		return hashCode;
	}
}
