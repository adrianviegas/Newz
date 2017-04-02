package adrian.news.model;

/**
 * Created by Adrian on 07/08/2016.
 */
public class Article {
    String image, title, content, date, category, tags, allowcomments;

    public Article(String title, String content, String category, String date, String image, String tags, String allowcomments) {
        this.allowcomments = allowcomments;
        this.category = category;
        this.content = content;
        this.date = date;
        this.image = image;
        this.tags = tags;
        this.title = title;
    }

    public String getAllowcomments() {

        return allowcomments;
    }

    public void setAllowcomments(String allowcomments) {
        this.allowcomments = allowcomments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
