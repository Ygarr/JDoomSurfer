import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class JSurfer extends JFrame {

  private JTabbedPane tabbedPane = new JTabbedPane();

  public JSurfer() {
    super("JSurfer Web Browser");

    createNewTab();

    getContentPane().add(tabbedPane);

    JMenu fileMenu = new JMenu("File");
    fileMenu.add(new NewTabAction());
    fileMenu.addSeparator();
    fileMenu.add(new ExitAction());
    fileMenu.setMnemonic('F');

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);

  }

  private void createNewTab() {
    JPanel panel = new JPanel(new BorderLayout());
    WebBrowserPane browserPane = new WebBrowserPane();
    WebToolBar toolBar = new WebToolBar(browserPane);
    panel.add(toolBar, BorderLayout.NORTH);
    panel.add(new JScrollPane(browserPane), BorderLayout.CENTER);
    tabbedPane.addTab("Browser " + tabbedPane.getTabCount(), panel);
  }

  private class NewTabAction extends AbstractAction {

    public NewTabAction() {
      putValue(Action.NAME, "New Browser Tab");
      putValue(Action.SHORT_DESCRIPTION, "Create New Web Browser Tab");
    //  putValue(Action.MNEMONIC_KEY, new Integer('N'));
    }

    public void actionPerformed(ActionEvent event) {
      createNewTab();
    }
  }

  private class ExitAction extends AbstractAction {
    public ExitAction() {
      putValue(Action.NAME, "Exit");
      putValue(Action.SHORT_DESCRIPTION, "Exit Application");
    //  putValue(Action.MNEMONIC_KEY, new Integer('x'));
    }

    public void actionPerformed(ActionEvent event) {
      System.exit(0);
    }
  }

  public static void main(String args[]) {
    JSurfer jsurfer = new JSurfer();
    jsurfer.setDefaultCloseOperation(EXIT_ON_CLOSE);
    jsurfer.setSize(640, 480);
    jsurfer.setVisible(true);
  }
}

class WebBrowserPane extends JEditorPane {

  private List history = new ArrayList();

  private int historyIndex;

  public WebBrowserPane() {
    setEditable(false);
  }

  public void goToURL(URL url) {
    displayPage(url);
    history.add(url);
    historyIndex = history.size() - 1;
  }

  public String correctURLString(String urlText) {
        final JTextField urlTextField = new JTextField(25);
        final String httpString = "https://";
        System.out.println("Incoming URL string:  " + urlText);
        if (urlText.indexOf(httpString) == -1) {
            urlText = httpString + IDN.toASCII(urlText);
        }
        urlTextField.setText(urlText);
        System.out.println("Corrected URL string:  " + urlText);
        return urlText;
    }

  public URL forward() {
    historyIndex++;
    if (historyIndex >= history.size())
      historyIndex = history.size() - 1;

    URL url = (URL) history.get(historyIndex);
    displayPage(url);

    return url;
  }

  public URL back() {
    historyIndex--;

    if (historyIndex < 0)
      historyIndex = 0;

    URL url = (URL) history.get(historyIndex);
    displayPage(url);

    return url;
  }

  private void displayPage(URL pageURL) {
    try {
      setPage(pageURL);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}

class WebToolBar extends JToolBar implements HyperlinkListener {

  private WebBrowserPane webBrowserPane;

  private JButton backButton;

  private JButton forwardButton;

  private JTextField urlTextField;

  public WebToolBar(WebBrowserPane browser) {
    super("Web Navigation");

    // register for HyperlinkEvents
    webBrowserPane = browser;
    webBrowserPane.addHyperlinkListener(this);

    urlTextField = new JTextField(25);
    urlTextField.addActionListener(new ActionListener() {

      // navigate webBrowser to user-entered URL
    public void actionPerformed(ActionEvent event) {

        try {
          URL url = new URL(urlTextField.getText());
          webBrowserPane.goToURL(url);
        }

        catch (MalformedURLException urlException) {
          urlException.printStackTrace();
        }
      }
    });

    backButton = new JButton("back");
    backButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        URL url = webBrowserPane.back();
        urlTextField.setText(url.toString());
      }
    });

    forwardButton = new JButton("forward");
    forwardButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        URL url = webBrowserPane.forward();
        urlTextField.setText(url.toString());
      }
    });
    
    final JButton goButton = new JButton("go");
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                try {
                    final String correctedString = WebToolBar.this.webBrowserPane.correctURLString(WebToolBar.this.urlTextField.getText());
                    final URL url = new URL(correctedString);
                    WebToolBar.this.webBrowserPane.goToURL(url);
                    WebToolBar.this.urlTextField.setText(url.toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    
    
    add(backButton);
    add(forwardButton);
    add(urlTextField);
    this.add(goButton, "East");
  }

  public void hyperlinkUpdate(HyperlinkEvent event) {
    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      URL url = event.getURL();
      webBrowserPane.goToURL(url);
      urlTextField.setText(url.toString());
    }
  }
}
