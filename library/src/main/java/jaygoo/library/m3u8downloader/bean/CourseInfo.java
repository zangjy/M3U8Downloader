package jaygoo.library.m3u8downloader.bean;

/**
 * 文件名：CourseInfo
 * 创建者：ZangJiaYu
 * 创建日期：2022/8/24
 * 描述：课程信息
 */
public class CourseInfo {
    /**
     * 班级Id
     */
    private String classId = "";
    /**
     * 课程Id
     */
    private String courseId = "";
    /**
     * 章Id
     */
    private String chapterId = "";
    /**
     * 节Id
     */
    private String sectionId = "";

    public CourseInfo(String classId, String courseId, String chapterId, String sectionId) {
        this.classId = classId;
        this.courseId = courseId;
        this.chapterId = chapterId;
        this.sectionId = sectionId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
