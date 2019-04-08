package com.hack.dumkahackathon;

import java.io.Serializable;

public class User implements Serializable {
    private String mName;
    private String mUid;
    private String mPincode;
    private String gender;
    private String dist;

    private long mBalance;

    private User() {}

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getPincode() {
        return mPincode;
    }

    public void updateBalance(final long balance) {
        mBalance = balance;
    }

    public long getBalance() {
        return mBalance;
    }

    public static Builder newUser() {
        return new Builder();
    }

    public static class Builder {

        private User mUser;

        private Builder() {
            mUser = new User();
        }

        public Builder setName(final String name) {
            mUser.mName = name;
            return this;
        }

        public Builder setUid(final String uid) {
            mUser.mUid = uid;
            return this;
        }

        public Builder setPincode(final String pincode) {
            mUser.mPincode = pincode;
            return this;
        }

        public Builder setGender(final String gender) {
            mUser.gender = gender;
            return this;
        }

        public Builder setDist(final String dist) {
            mUser.dist = dist;
            return this;
        }

        public User create() {
            return mUser;
        }

    }
}
