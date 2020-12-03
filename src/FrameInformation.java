import java.awt.image.BufferedImage;

public class FrameInformation {

    private BufferedImage image;
    private BufferedImage originalImage;
    private String fileName;

    public FrameInformation(BufferedImage image, String fileName)
    {
        this.originalImage = image;
        this.image = image;
        this.fileName = fileName;

    }

    // Getters and Setters


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }
}
