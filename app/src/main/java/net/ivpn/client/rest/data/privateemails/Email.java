package net.ivpn.client.rest.data.privateemails;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Email implements Parcelable{
    @SerializedName(value="email", alternate={"source"})
    @Expose
    private String email;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("note")
    @Expose
    private String note;

    public static final Creator<Email> CREATOR = new Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel in) {
            return new Email(in);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }
    };

    public Email(String email, String note) {
        this.email = email;
        this.note = note;
    }

    protected Email(Parcel in) {
        email = in.readString();
        type = in.readString();
        note = in.readString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Email)) return false;
        return this.email.equals(((Email) obj).email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(type);
        parcel.writeString(note);
    }
}
