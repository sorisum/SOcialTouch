package fr.socialtouch.android.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

import com.facebook.utils.SessionStore;

import fr.socialtouch.android.R;

public class FacebookUser implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String MALE = "male";
	public static final String FEMALE = "female";

	private final String TAG = this.getClass().getName();
	private Context mContext;

	public ArrayList<FacebookLike> mListLike = new ArrayList<FacebookLike>();
	public String mID = "";
	public String mName = "";
	public String mUsername = ""; // correspond au pseudo
	public String mBirthday = "";
	public String mGender = "";
	public String mReligion = "";
	public AddressSOcialTouch mLocation = new AddressSOcialTouch();
	public AddressSOcialTouch mHomeTown = new AddressSOcialTouch();
	public String mSocialTouchTag = "";
	private ArrayList<String> mListCommonPoints = new ArrayList<String>();

	private double mCompatibityRate = -1;

	public FacebookUser(Context context) {
		mContext = context;
	}

	public String toZip() {
		return "zipped";
	}

	public String toUpload() {
		return "uploaded";
	}

	@Override
	public String toString() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("id", mID);
		table.put("username", mUsername);
		table.put("birthday", mBirthday);
		table.put("hometown", mHomeTown.toString());
		table.put("gender", mGender);
		table.put("religion", mReligion);
		table.put("location", mLocation.toString());
		String str = "(" + mListLike.size() + "){";
		for (FacebookLike fblike : mListLike) {
			str += fblike.mName + ",";
		}
		str += "}";
		table.put("listLike", str);
		return table.toString();
	}

	/**
	 * You should run {@link #getCompatibilityWith(FacebookUser)} first.
	 */
	public int getPointsCommumNumber() {
		return mListCommonPoints.size();
	}

	/**
	 * You should run {@link #getCompatibilityWith(FacebookUser)} first.
	 */
	public ArrayList<String> getPointsCommunsListe() {
		return mListCommonPoints;
	}

	/**
	 * You should run {@link #getCompatibilityWith(FacebookUser)} first.
	 */
	public double getCompatibility() {
		return mCompatibityRate;
	}

	public double getCompatibilityWith(FacebookUser profile) {
		mCompatibityRate = 0;
		double scoreMax = 0;
		mListCommonPoints.clear();

		if (!this.mBirthday.equals("") && !profile.mBirthday.equals("")) {
			try {
				Date birthday = new SimpleDateFormat("MM/dd/yyyy").parse(this.mBirthday);
				Date birthdayProfile = new SimpleDateFormat("MM/dd/yyyy").parse(profile.mBirthday);
				scoreMax += 6;
				if (birthday.getTime() == birthdayProfile.getTime()) {
					mListCommonPoints.add(mContext.getString(R.string.point_commun_anniversaire));
					mCompatibityRate += 6;
				} else if (Math.abs(birthday.getYear() - birthdayProfile.getYear()) <= 5)
					mCompatibityRate += 6;
				else if (Math.abs(birthday.getYear() - birthdayProfile.getYear()) <= 10)
					mCompatibityRate += 3;
				else if (Math.abs(birthday.getYear() - birthdayProfile.getYear()) <= 20)
					mCompatibityRate += 1;
			} catch (Exception e) {
				Log.w(TAG, e.getMessage());
			}
		}

		if (!this.mReligion.equals("") && !profile.mReligion.equals("")) {
			scoreMax += 2;
			if (this.mReligion.equalsIgnoreCase(profile.mReligion)) {
				mCompatibityRate += 2;
				mListCommonPoints.add(mContext.getString(R.string.point_commun_religion).replace(
						"@r", this.mReligion));
			}
		}

        if (!this.mLocation.mTown.isEmpty() && !profile.mLocation.mTown.isEmpty()) {
            scoreMax += 8;
            if (this.mLocation.mTown.equals(profile.mLocation.mTown)) {
                mCompatibityRate += 8;
                mListCommonPoints.add(mContext.getString(R.string.point_commun_danslecoin));
            } 
        }

        if (!this.mHomeTown.mTown.isEmpty() && !profile.mHomeTown.mTown.isEmpty()) {
            scoreMax += 5;
            if (this.mHomeTown.mTown.equals(profile.mHomeTown.mTown)) {
                mCompatibityRate += 5;
                mListCommonPoints
                        .add(mContext.getString(R.string.point_commun_danslecoin_hometown));
            } 
        }

		if (!this.mBirthday.equals("") && !profile.mBirthday.equals("")) {
			scoreMax += 6;
			if (getAstro() == profile.getAstro()) {
				mCompatibityRate += 6;
				mListCommonPoints.add(mContext.getString(R.string.point_commun_astro));
			}
		}

		scoreMax += this.mListLike.size();
		for (FacebookLike fblike : this.mListLike) {
			for (int i = 0; i < profile.mListLike.size(); i++) {
				if (fblike.mName.equalsIgnoreCase(profile.mListLike.get(i).mName)) {
					mListCommonPoints.add(fblike.mName);
					mCompatibityRate += 1;
					break;
				}
			}
		}

		Collections.shuffle(mListCommonPoints);
		// Log.v(TAG,
		// "computeCompatibilityWith("+profile.mUsername+") => score:"+score+", scoreMax="+scoreMax);
		mCompatibityRate = mCompatibityRate / scoreMax * 100;

		return mCompatibityRate;
	}

    private int getAstro() {
        Date birthday;
        int returnInt;
        try {
            birthday = new SimpleDateFormat("MM/dd/yyyy").parse(this.mBirthday);
        } catch (ParseException e) {
            Log.w(TAG, "getAstro() => " + e.getMessage());
            return -1;
        }
        if ((birthday.getMonth() == 2 && birthday.getDate() >= 21) || (birthday.getMonth() == 3
                && birthday.getDate() <= 20))
            returnInt = 0;
        else if ((birthday.getMonth() == 3 && birthday.getDate() >= 21) || (birthday.getMonth() == 4
                && birthday.getDate() <= 20))
            returnInt = 1;
        else if ((birthday.getMonth() == 4 && birthday.getDate() >= 21) || (birthday.getMonth() == 5
                && birthday.getDate() <= 21))
            returnInt = 2;
        else if ((birthday.getMonth() == 5 && birthday.getDate() >= 22) || (birthday.getMonth() == 6
                && birthday.getDate() <= 22))
            returnInt = 3;
        else if ((birthday.getMonth() == 6 && birthday.getDate() >= 23) || (birthday.getMonth() == 7
                && birthday.getDate() <= 22))
            returnInt = 4;
        else if ((birthday.getMonth() == 7 && birthday.getDate() >= 23) || (birthday.getMonth() == 8
                && birthday.getDate() <= 22))
            returnInt = 5;
        else if ((birthday.getMonth() == 8 && birthday.getDate() >= 23) || (birthday.getMonth() == 9
                && birthday.getDate() <= 23))
            returnInt = 6;
        else if ((birthday.getMonth() == 9 && birthday.getDate() >= 24) || (birthday.getMonth() == 10
                && birthday.getDate() <= 22))
            returnInt = 7;
        else if ((birthday.getMonth() == 10 && birthday.getDate() >= 23) || (birthday.getMonth() == 11
                && birthday.getDate() <= 21))
            returnInt = 8;
        else if ((birthday.getMonth() == 11 && birthday.getDate() >= 22) || (birthday.getMonth() == 0
                && birthday.getDate() <= 20))
            returnInt = 9;
        else if ((birthday.getMonth() == 0 && birthday.getDate() >= 21) || (birthday.getMonth() == 1
                && birthday.getDate() <= 19))
            returnInt = 10;
        else if ((birthday.getMonth() == 1 && birthday.getDate() >= 20) || (birthday.getMonth() == 2
                && birthday.getDate() <= 20))
            returnInt = 11;
        else
            returnInt = -1;
        // Log.v(TAG, "getAstro() => "+returnInt);
        return returnInt;
    }

	public static FacebookUser readObject(Context context, String facebookUserFormatted) {
		FacebookUser user = new FacebookUser(context);
		String value = "#";
		StringTokenizer token = new StringTokenizer(facebookUserFormatted, "|");
		// username
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mUsername = value;
		}
		// name
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mName = value;
		}
		// gender
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			if (value.equals("" + SessionStore.MALE)) {
				user.mGender = MALE;
			} else {
				user.mGender = FEMALE;
			}
		}
		// so tag
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mSocialTouchTag = value;
		}
		// birthday
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mBirthday = value;
		}
		// town
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mLocation.mTown = value;
		}
		// home town
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mHomeTown.mTown = value;
		}
		// religion
		value = "#";
		value = token.nextToken();
		if (!value.equals("#")) {
			user.mReligion = value;
		}
		// likes
		FacebookLike like = null;
		while (token.hasMoreElements()) {
			value = "#";
			value = token.nextToken();
			if (!value.equals("#")) {
				like = new FacebookLike();
				like.mName = value;
				user.mListLike.add(like);
			}
		}
		Log.e("TEST DECODE", user.toString());
		return user;
	}
}
