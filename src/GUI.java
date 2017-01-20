import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by xdcao on 2017/1/20.
 */
public class GUI extends JFrame {

    public static void main(String[] args){
        GUI gui=new GUI("动漫相片");
    }

    private JTextField openAddress;
    private JTextField saveAddress;
    private JTextField choosenFile;
    private JTextField savedFile;
    private JButton choose;
    private JButton save;
    private JButton start;

    private FileDialog openDia,saveDia;
    private File openfile;
    private File saveFile;


    public GUI(String str){
        super(str);
        Container contentPane=this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        openDia = new FileDialog(this, "打开", FileDialog.LOAD);
        saveDia = new FileDialog(this, "保存", FileDialog.SAVE);

        JPanel choosePanel=new JPanel();
        choosePanel.setLayout(new FlowLayout());
        openAddress=new JTextField("打开文件位置");
        openAddress.setEditable(false);
        choosenFile=new JTextField("                                                                                    ");
        choosenFile.setEditable(true);
        choose=new JButton("浏览");
        choosePanel.add(openAddress);
        choosePanel.add(choosenFile);
        choosePanel.add(choose);
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDia.setVisible(true);//显示打开文件对话框

                String dirpath = openDia.getDirectory();//获取打开文件路径并保存到字符串中。
                String fileName = openDia.getFile();//获取打开文件名称并保存到字符串中

                if (dirpath == null || fileName == null)//判断路径和文件是否为空
                    return;
                else
                    choosenFile.setText(null);//文件不为空，清空原来文件内容。
                openfile = new File(dirpath, fileName);//创建新的路径和名称
                choosenFile.setText(dirpath+fileName);
            }
        });


        JPanel savePanel=new JPanel();
        savePanel.setLayout(new FlowLayout());
        saveAddress=new JTextField("保存文件位置");
        saveAddress.setEditable(false);
        savedFile=new JTextField("                                                                                    ");
        savedFile.setEditable(true);
        save=new JButton("浏览");
        savePanel.add(saveAddress);
        savePanel.add(savedFile);
        savePanel.add(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (saveFile == null) {
                    saveDia.setVisible(true);//显示保存文件对话框
                    String dirpath = saveDia.getDirectory();//获取保存文件路径并保存到字符串中。
                    String fileName= saveDia.getName()+".png";
                    if (dirpath == null || fileName == null)//判断路径和文件是否为空
                        return;//空操作
                    else
                        saveFile = new File(dirpath, fileName);//文件不为空，新建一个路径和名称
                    savedFile.setText(dirpath+fileName);
                }
            }
        });

        start=new JButton("start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cartoon.execute(openfile,saveFile);
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        contentPane.add(choosePanel, BorderLayout.NORTH);
        contentPane.add(savePanel,BorderLayout.CENTER);
        contentPane.add(start,BorderLayout.SOUTH);
        setSize(500, 150);
        setVisible(true);

    }


}
