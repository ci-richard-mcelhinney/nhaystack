package nhaystack.ui.view;

import java.io.File;

import javax.baja.gx.BImage;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.pane.*;
import javax.baja.ui.wizard.*;
import javax.baja.util.*;
import javax.baja.workbench.BWbShell;

import com.tridium.workbench.fieldeditors.BPasswordFE;

public class BCustomDictWizard
    extends WizardModel
{

/////////////////////////////////////////////////////////////////
//  Constructor
/////////////////////////////////////////////////////////////////
  
  public BCustomDictWizard(BWbShell shell)
  {
    this.shell = shell;
    fileDirBase = Sys.getBajaHome() + File.separator;
    buildStep0();
  }

  /**
   * Create the Edge pane for Step 0
   */
  private void buildStep0()
  {
    fileName = new BTextField("", 20);
    fileDir = new BTextField(fileDirBase, 50, false);
    
    BGridPane grid = new BGridPane(1);
    grid.setHalign(BHalign.left);
    
    grid.add(null, new BLabel(FILE_NAME));
    grid.add(null, fileName);
    
    grid.add(null, new BNullWidget());
    grid.add(null, new BNullWidget());
    
    grid.add(null, new BLabel(FILE_DIR));
    grid.add(null, fileDir);
    
    BBorderPane border = new BBorderPane(grid, 10, 10, 10, 10);
    step0 = new BEdgePane();
    step0.setTop(new BWizardHeader(BANNER, WIZ_HEADER_STEP1));
    step0.setCenter(border);
  }
  
  
/////////////////////////////////////////////////////////////////
//  Wizard Model
/////////////////////////////////////////////////////////////////  
  
  /**
   * Provides the title of our wizard view
   */
  public String getTitle()
  {
    return TITLE;
  }

  /**
   * Initializes our wizard with the first step of the wizard.
   */
  public void init()
  {
    updateToStep0();
  }
  
  /**
   * Navigates our wizard steps back by one step
   */
  public void back()
  {
    switch(currentStep)
    {
      case 1: updateToStep0(); break;
    }
  }

  /**
   * Navigates our wizard steps forward by one step
   */
  public void next()
  {
    switch(currentStep)
    {
      case 0: updateToStep0(); break;

    }
  }
  
  /**
   * Completes our task and returns true to close the wizard dialog.
   */
  public boolean finish()
  {
    try
    {
      //build our message content
      String name = fileName.getText();
      String dir = fileDir.getText();
      
      BGridPane results = new BGridPane(1);
      results.add(null, new BLabel("Creating station named: " + name ));
      results.add(null, new BLabel("Directory: " + dir ));
      
      BBorderPane border = new BBorderPane(results,10,10,10,10);
      BDialog.open(shell, "Results", border, BDialog.OK);

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    return true;
  }
  
/////////////////////////////////////////////////////////////////
//  Updates
/////////////////////////////////////////////////////////////////  
  
  /**
   * Updates the wizard view to the first step of our wizard. If the
   * station name is a valid name, we enable the Finish button.
   */
  private void updateToStep0()
  {
    String name = fileName.getText();
    int mask = (SlotPath.isValidName(name) && name.length() <= 32) ? CAN_FINISH : 0;
    update(step0, CAN_NEXT | mask );
    getWizard().setNextAsDefault();
    currentStep = 0;
  }


  
/////////////////////////////////////////////////////////////////
//  Attributes
/////////////////////////////////////////////////////////////////  
  private static final Lexicon lex = Lexicon.make(BCustomDictWizard.class);
  
  private static final String TITLE = lex.getText("wizard.example.title");
  private static final String FILE_NAME = lex.getText("wizard.station.name");
  private static final String FILE_DIR = lex.getText("wizard.station.dir");
  private static final String STATION_PASS_1 = lex.getText("wizard.station.pass.1");

  
  private static final String WIZ_HEADER_STEP1 = lex.getText("wizard.station.step1");

  
  private static final BIcon ICON = BIcon.make(BOrd.make("module://nhaystack/nhaystack/icons/tag.png"));
  private static final BImage BANNER = BImage.make(ICON);
  
  private BWbShell shell;
  
  private BEdgePane step0;
  private BEdgePane step1;
  private BEdgePane step2;
  
  private int currentStep = 0;
  private String fileDirBase;
  
  private BTextField fileName;
  private BTextField fileDir;
  private BPasswordFE adminPass1;
  private BPasswordFE adminPass2;
  private BTextField foxPort;
  private BTextField httpPort;
}
