package info.passdaily.camrelconvertapp.services.initsdk;

public interface AuthConstants {

	// TODO Change it to your web domain
	public final static String WEB_DOMAIN = "zoom.us";

	// TODO Change it to your APP Key
	public final static String SDK_KEY = "I2I3TPAxkrFEs657QqAzp4TEyvUIPutNSkHs";
	//I2I3TPAxkrFEs657QqAzp4TEyvUIPutNSkHs

	// TODO Change it to your APP Secret
	public final static String SDK_SECRET = "UWDSUF33H1wSUVEt7jdTcjgUb4br5BwRN54n";
	//UWDSUF33H1wSUVEt7jdTcjgUb4br5BwRN54n

	/*
	 * We recommend that, you can generate jwttoken on your own server instead of hardcore in the code.
	 * We hardcore it here, just to run the demo.
	 *
	 * You can generate a jwttoken on the https://jwt.io/
	 * with this payload:
	 * {
	 *     "appKey": "string", // app key
	 *     "iat": long, // access token issue timestamp
	 *     "exp": long, // access token expire time
	 *     "tokenExp": long // token expire time
	 * }
	 */

	public final static String SDK_JWTTOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOm51bGwsImlzcyI6IlltT0ZINm9XU3llclVtV3FCN0hoVVEiLCJleHAiOjE2NTQxNzgxMDAsImlhdCI6MTU5MDUwMTM4OX0._9mpfheaBRp2cvA5dAjpMd0fCU_L3T9QEbGJp71Cvn4"; //YOUR JWTTOKEN
//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOm51bGwsImlzcyI6IlltT0ZINm9XU3llclVtV3FCN0hoVVEiLCJleHAiOjE2NTQxNzgxMDAsImlhdCI6MTU5MDUwMTM4OX0._9mpfheaBRp2cvA5dAjpMd0fCU_L3T9QEbGJp71Cvn4
	///Token will be expired in two years on the date of 26-05-2021 19:25.
}
