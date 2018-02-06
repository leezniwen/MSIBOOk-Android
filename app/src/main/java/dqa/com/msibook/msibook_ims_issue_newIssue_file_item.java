package dqa.com.msibook;

import android.graphics.Bitmap;

/**
 * Created by androids on 2016/11/7.
 */
public class msibook_ims_issue_newIssue_file_item {

    public enum FileType {

        Image(0),
        Video(1),
        Voice(2);

        private int value;

        private FileType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    Bitmap ImageBitMap;

    String ImageName;

    String ImagePath;

    String VideoPath;

    String VoicePath;

    FileType _FileType;

    public msibook_ims_issue_newIssue_file_item(Bitmap ImageBitMap, String ImageName, String ImagePath, String VideoPath, String VoicePath, FileType _FileType) {
        this.ImageBitMap = ImageBitMap;

        this.ImagePath = ImagePath;

        this.ImageName = ImageName;

        this.VideoPath = VideoPath;

        this.VoicePath = VoicePath;

        this._FileType = _FileType;

    }

    public Bitmap GetImageBitMap() {
        return this.ImageBitMap;
    }

    public String GetImageName() {
        return this.ImageName;
    }

    public String GetVideoPath() {
        return this.VideoPath;
    }

    public String GetVoicePath() {
        return this.VoicePath;
    }

    public String GetImagePath() {
        return this.ImagePath;
    }

    public FileType GetFileType() {
        return this._FileType;
    }
}
