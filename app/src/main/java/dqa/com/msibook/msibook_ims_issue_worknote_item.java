package dqa.com.msibook;

/**
 * Created by androids on 2016/10/21.
 */
public class msibook_ims_issue_worknote_item {

    String Author;

    String Author_WorkID;

    String Date;

    String Content;

    String AuthorImage;

    String File;

    public msibook_ims_issue_worknote_item(String Author, String Author_WorkID, String Date, String Content, String AuthorImage, String File) {
        this.Author = Author;

        this.Author_WorkID = Author_WorkID;

        this.Date = Date;

        this.Content = Content;

        this.AuthorImage = AuthorImage;

        this.File = File;

    }

    public String GetAuthor() {
        return this.Author;
    }

    public String GetAuthor_WorkID() {
        return this.Author_WorkID;
    }

    public String GetDate() {
        return this.Date;
    }

    public String GetContent() {
        return this.Content;
    }

    public String GetAuthorImage() {
        return this.AuthorImage;
    }

    public String GetFile() {
        return this.File;
    }
}
