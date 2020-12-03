import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainSystem {

    private ImageUtil pixelator;
    private ArrayList<FrameInformation> frames;
    private int selectedFrame;

    public  MainSystem()
    {
        pixelator = new ImageUtil();
        frames = new ArrayList<FrameInformation>();
        selectedFrame = 0;

    }

    private Image getScaledImage(Image srcImg, int w, int h){

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }


    public ImageIcon createLabelIcon(String location, int width) throws IOException {

        ImageIcon image = new ImageIcon(location);
        int height = calculateNewImageHeight(image.getIconHeight(),image.getIconWidth(),width);

        if(height > 500)
        {
          return  createLabelIcon(location,width - 50);
        }

        image.setImage(image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));

        return image;
    }

    public ImageIcon getSelectedFrameImage(int width)
    {
        ImageIcon image = new ImageIcon(getFrames().get(getSelectedFrame()).getImage());
        int height = calculateNewImageHeight(image.getIconHeight(),image.getIconWidth(),width);

        if(height > 500)
        {
            return  getSelectedFrameImage(width - 50);
        }
        image.setImage(image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));

        return image;
    }

    public void addAnimationFrames(String folderLocation) throws IOException, URISyntaxException {
        ArrayList<String> componentPaths = listExternalFilesInDir(folderLocation);
        String parentFolderName = new File(folderLocation).getParentFile().getName();

        addExternalImagesAtLocation(folderLocation, componentPaths);
    }

    public ArrayList<String> listExternalFilesInDir(String location) throws URISyntaxException, IOException {
        ArrayList<String> paths = new ArrayList<String>();

        //Creating a File object for directory
        File directoryPath = new File(location);
        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {

            if(!file.getName().startsWith( "."))
            {
                paths.add(file.getName());
            }

        }
        return paths;

    }

    public void addExternalImagesAtLocation(String folderLocation, ArrayList<String> names) throws IOException {
        BufferedImage image;
        String name;
        ArrayList<String> sortedFileNames = new ArrayList<String>();

        ArrayList<String> fileNames = removeExtension(names, ".png");

        //sort the file locations by containing number
        String[] frames = fileNames.toArray(new String[0]);
        sortedFileNames = sortStringArrayInAscendingOrder(frames);


        //add Frame images and information to arraylist
        setFrames(new ArrayList<FrameInformation>());

        for (int i = 0; i < sortedFileNames.size(); i++) {

            //read image from location
            try{
                File imageFile = new File(folderLocation + "/" + sortedFileNames.get(i) + ".png");

                image = ImageIO.read(imageFile);
                name = imageFile.getName();

                //Save frame to arraylist
                getFrames().add(new FrameInformation(image,name));

            }
            catch(Exception ex)
            {

            }
        }
    }

    public ArrayList<String> sortStringArrayInAscendingOrder(String[] array)
    {
        //sort array using custom comparator
        Arrays.sort(array, new StringNumberComparator());

        ArrayList<String> list1 = new ArrayList<String>();
        Collections.addAll(list1, array);

        return list1;
    }

    public void createGifFromFrames(String folderPath, String firstFileName, String createdGifLocation, int delay) throws IOException {
        BufferedImage first = getFrames().get(0).getImage();
        ImageOutputStream output = new FileImageOutputStream(new File(createdGifLocation));


        GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), delay, true);
        writer.writeToSequence(first);

        ArrayList<BufferedImage> frameImages = new ArrayList<BufferedImage>();
        for(int i=1; i<getFrames().size(); i++)
        {
            frameImages.add(getFrames().get(i).getImage());
        }

        BufferedImage[] images = frameImages.toArray(new BufferedImage[0]);

        for (BufferedImage image : images) {
            writer.writeToSequence(image);
        }

        writer.close();
        output.close();
    }


    public ArrayList<String> removeExtension(ArrayList<String> list, String extension)
    {
        ArrayList<String> newList = new ArrayList<String>();
        for(int i=0; i<list.size(); i++)
        {
            String item = list.get(i).replace(extension, "");
            newList.add(item);
        }

        return newList;
    }

    public BufferedImage pixelateImage(BufferedImage image, int pixelationStrength)
    {
        BufferedImage pixelatedImage = getPixelator().pixelate(image, pixelationStrength);

        return pixelatedImage;
    }

    public void pixelateAllFrames(int pixelationStrength)
    {
        for (int i=0; i<getFrames().size(); i++)
        {
            getFrames().get(i).setImage(pixelateImage(getFrames().get(i).getOriginalImage(),pixelationStrength));
        }
    }

    public void saveAnimationAsFiles(ArrayList<FrameInformation> frames, String path, String fileName)
    {

        for(int i=0; i< frames.size(); i++)
        {
            // Save frame as new image
            try {
                ImageIO.write(frames.get(i).getImage(), "PNG", new File(path, fileName + (i+1) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void saveAnimationAsSingleFile(ArrayList<FrameInformation> frames, String path, String fileName)
    {
        BufferedImage frameStrip = frames.get(0).getImage();
        for(int i=1; i< frames.size(); i++)
        {
            frameStrip = joinBufferedImage(frameStrip,frames.get(i).getImage());
        }

        // Save strip as new image
        try {
            ImageIO.write(frameStrip, "PNG", new File(path, fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage joinBufferedImage(BufferedImage img1,BufferedImage img2) {

        //do some calculate first
        int offset  = 0;
        int wid = img1.getWidth()+img2.getWidth()+offset;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth()+offset, 0);
        g2.dispose();
        return newImage;


    }

    public int calculateNewImageHeight(int originalHeight, int originalWidth, int newWidth)
    {
        float number = ((float) originalHeight / originalWidth) * newWidth;
        return Math.round(number);
    }

    // Getters and Setters


    public ArrayList<FrameInformation> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<FrameInformation> frames) {
        this.frames = frames;
    }

    public int getSelectedFrame() {
        return selectedFrame;
    }

    public void setSelectedFrame(int selectedFrame) {
        this.selectedFrame = selectedFrame;
    }

    public ImageUtil getPixelator() {
        return pixelator;
    }

    public void setPixelator(ImageUtil pixelator) {
        this.pixelator = pixelator;
    }
}
