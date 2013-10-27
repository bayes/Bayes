/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package image;

import bayes.DirectoryManager;
import bayes.JPreferences;
import interfacebeans.Viewer;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import utilities.DisplayText;
import utilities.IO;
import utilities.ImageUtility;

   class ImageViewerPopupMenu extends  JPopupMenu{

     public static enum SAVE_TYPE{
        JPEG                    (   "jpeg"  ),
        GIF                     (   "gif"  ),
        BMP                     (   "bmp"  ),
        WBMP                    (   "wbmp"  ),
        PNG                     (   "png"  );


        private final String name;
        SAVE_TYPE(String aname) {this.name       = aname;}
        public String getName() {return name;}

    }


    ImageViewer imageViewer;
    public static File saveDir              =   null;
    JMenuItem resetMenuItem                 =   new JMenuItem ("Display Full Image");
    JMenuItem delteMenuItem                 =   new JMenuItem ("Delete Selected");
    JMenuItem delteALLMenuItem              =   new JMenuItem ("Delete All");
    JMenuItem saveSelectedMenuItem          =   new JMenuItem ("Copy Selected Images As");
    JMenuItem saveAllMenuItem               =   new JMenuItem ("Copy All Images As");
    JMenuItem setGrayScaleMenuItem          =   new JMenuItem ("Autoset Grayscale For Entire Stack");
    JMenuItem setGrayScaleFormSingleImgMenuItem = new JMenuItem ("Autoset Grayscale For Current Image");
    JMenuItem viewPixelsMenuItem            =   new JMenuItem ("View Selected Pixels as Text");
    JMenuItem loadPixelsMenuItem            =   new JMenuItem ("Load Selected Pixels");
    JMenuItem pixelsHistogramMenuItem       =   new JMenuItem ("Show Histogram");
    JMenuItem infoMenuItem                  =   new JMenuItem ("Show info");
    JMenuItem exportToImageMenuItem         =   new JMenuItem ("Export to ImageJ");
    JMenuItem prefsMenuItem                 =   new JMenuItem ("Image Viewer Settings");
    
    JMenu saveMenu                          =   new JMenu("Save Displayed Image As");
    JMenuItem jpegMenuItem                  =   new JMenuItem ("jpeg");
    JMenuItem pngMenuItem                   =   new JMenuItem ("png");
    JMenuItem gifMenuItem                  =   new JMenuItem ("gif");
    JMenuItem bmpMenuItem                   =   new JMenuItem ("bmp");
    JMenuItem wbmpMenuItem                  =   new JMenuItem ("wbmp");
  
    public void addListeners() {
       resetMenuItem .addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            imageViewer.getImagePane ().unZoomImage ();
            imageViewer.updateImage ();
           }
        });
       delteMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {


             List <File> files         =    imageViewer.getSelectedImgAndIfhFiles();
             for (File file : files) {
                file.delete();
             }


             int getCurIndex         =   imageViewer.getImageJList ().getSelectedIndex();
             imageViewer.updateImageList(getCurIndex);
           }
        });
       delteALLMenuItem .addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            int size                        =   imageViewer .getImageJList ().getModel().getSize();
            for (int curElemIndex = size - 1 ; curElemIndex >= 0 ; curElemIndex--) {
                File imgFile                =   ( File)imageViewer.getImageJList ().getModel().getElementAt(curElemIndex);
                File ifhFile                =    DirectoryManager.getIfhFileForImage(imgFile);
                imgFile.delete();
                ifhFile .delete();
            }

             imageViewer.updateImageList();
           }
        });
       saveSelectedMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            List<File> files          =    imageViewer.getSelectedImgAndIfhFiles();
            saveImages(files);
           }
        });
       saveAllMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            List <File>files      =     DirectoryManager. getImgAndIfhFileListOnDisk();
            saveImages(files);
           }
        });

       jpegMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            Image image          =    imageViewer.getDisplayedImage();
            saveImage(image, SAVE_TYPE.JPEG.getName());
           }
        });
       pngMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            Image image          =    imageViewer.getDisplayedImage();
            saveImage(image, SAVE_TYPE.PNG.getName());
           }
        });
       gifMenuItem.addActionListener (new ActionListener (){
       public void actionPerformed (ActionEvent e) {
            Image image          =    imageViewer.getDisplayedImage();
            saveImage(image, SAVE_TYPE.GIF.getName());
           }
        });
       bmpMenuItem.addActionListener (new ActionListener (){
       public void actionPerformed (ActionEvent e) {
            Image image          =    imageViewer.getDisplayedImage();
            saveImage(image, SAVE_TYPE.BMP.getName());
           }
        });
       wbmpMenuItem.addActionListener (new ActionListener (){
       public void actionPerformed (ActionEvent e) {
            Image image          =    imageViewer.getDisplayedImage();
            saveImage(image, SAVE_TYPE.WBMP.getName());
           }
        });


       setGrayScaleMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
             imageViewer.autoscaleColorScaleForStack();
           }
        });
       setGrayScaleFormSingleImgMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
             imageViewer.autoscaleColorScaleForCurrentImage();
           }
        });
       viewPixelsMenuItem .addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {

                ImageViewer.getInstance().viewPixelsForHistogram();
           }
        });
       loadPixelsMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
                ImageViewer.getInstance().loadPixelsFromRoi();
           }
        });
       pixelsHistogramMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
                ImageViewer.getInstance().histogramPixels();
           }
        });


        prefsMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
              JPreferences.displayImagePreferences();
           }
        });

       infoMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
           Viewer.display( imageViewer.getInfo(), "Image Info");
        }});

        exportToImageMenuItem.addActionListener (new ActionListener (){
        public void actionPerformed (ActionEvent e) {
            imageViewer.exportHyperStackToImageJ();
        }});
    }
    public ImageViewerPopupMenu() {
        this(true);
    }
    public ImageViewerPopupMenu(boolean includeSingleItemMenus) {
        super();
        imageViewer                     =    ImageViewer.getInstance();
        addListeners();

        if (includeSingleItemMenus){
            add( resetMenuItem );
            add( delteMenuItem);
            add( delteALLMenuItem );
            add(  setGrayScaleMenuItem);
            add(  setGrayScaleFormSingleImgMenuItem);
            add( new JSeparator() );
            add( viewPixelsMenuItem );
            add( loadPixelsMenuItem);
            add(pixelsHistogramMenuItem);
            add( new JSeparator() );
            add( saveSelectedMenuItem );
            add( saveAllMenuItem);
            add (saveMenu);
                saveMenu.add(jpegMenuItem );
                saveMenu.add(pngMenuItem );
                saveMenu.add( gifMenuItem  );
                saveMenu.add( bmpMenuItem  );
               // saveMenu.add( wbmpMenuItem  );
            add( new JSeparator() );
            add( exportToImageMenuItem);
            add( new JSeparator() );
            add ( infoMenuItem);
            add (prefsMenuItem);




        }
        else{
            add( delteMenuItem);
            add( delteALLMenuItem );
            add( saveSelectedMenuItem );
            add(saveAllMenuItem);
        }

    }
    public void saveImages( List<File> files    ){
           if (files.isEmpty()){
                DisplayText.popupErrorMessage("No images were selected.\n Exit...");
                return;
            }

            JFileChooser fc             = new JFileChooser (saveDir  );
            fc.setMultiSelectionEnabled (false);
            fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            //fc.setDialogType(JFileChooser.SAVE_DIALOG );
            fc.setDialogTitle("Save image");
            fc.setApproveButtonText("Save");
            int returnVal                   =   fc.showOpenDialog (fc);
            if (returnVal                   !=   JFileChooser.APPROVE_OPTION) { return;}
            File dir                        =   fc.getSelectedFile ();
            saveDir                         =   dir;

            List <File> dstFiles            =   new ArrayList<File>();
            for (File src : files) {
                File dst                    =     new File (dir,src.getName() );
                boolean savedImageFile      =   IO.copyFile(src, dst);
                if (savedImageFile == true){
                    dstFiles.add(dst);
                }
                else {
                    for (File file : dstFiles) {
                        file.delete();
                    }
                    DisplayText.popupErrorMessage("Failed to save image(s).");
                }

            }





    }
    public void saveImage(Image image, String type   ){
           if (image  == null ){
                DisplayText.popupErrorMessage("No image is selected.\n Exit...");
                return;
            }

            JFileChooser fc             = new JFileChooser (saveDir  );
            fc.setMultiSelectionEnabled (false);
            fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            //fc.setDialogType(JFileChooser.SAVE_DIALOG );
            fc.setDialogTitle("Save image");
            fc.setApproveButtonText("Save");
            int returnVal                   =    fc.showOpenDialog (fc);
            if (returnVal                   !=   JFileChooser.APPROVE_OPTION) { return;}

            File  selected                   =   fc.getSelectedFile ();
            File dir                         =  null;
            String imgName                   =  null;


            if(selected.isDirectory()) {
                dir                             =   fc.getSelectedFile ();
                imgName                  =   "savedBayesInterface."+ type ;
            }
            else{
                dir                             =   selected.getParentFile();
                imgName                         =   selected.getName();
                if (imgName.endsWith(type) == false){
                    imgName                     =   imgName  +"."+type;
                }
            }



            saveDir                         =   dir;

            BufferedImage bi                   = ImageUtility.toBufferedImage(image);
            try {
                File outputfile = new File(dir, imgName );
                javax.imageio.ImageIO.write(bi,  type, outputfile);
            } catch (IOException e) {
                DisplayText.popupErrorMessage("Failed to save image.\n Exit...");
            }

    }

}//JPreferences