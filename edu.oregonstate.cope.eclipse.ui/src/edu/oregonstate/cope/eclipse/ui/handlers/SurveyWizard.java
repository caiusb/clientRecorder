package edu.oregonstate.cope.eclipse.ui.handlers;

import java.math.BigInteger;
import java.util.Random;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.json.simple.JSONObject;

import edu.oregonstate.cope.clientRecorder.installer.SurveyProvider;

public class SurveyWizard extends Wizard implements SurveyProvider {

	protected SurveyPage surveyPage;

	private JSONObject surveyAnswers;
	private String email;

	private String onid;

	public SurveyWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public static SurveyProvider takeRealSurvey() {
		SurveyWizard sw = new SurveyWizard();
		WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), sw);

		wizardDialog.open();

		return sw;
	}
	
	public static SurveyProvider takeFakeSurvey(){
		return new SurveyProvider() {
			
			@Override
			public String getSurveyResults() {
				return "test survey";
			}
			
			@Override
			public String getEmail() {
				return "test email";
			}

			@Override
			public String getOnid() {
				return "test onid";
			}
		};
	}

	@Override
	public void addPages() {
		surveyPage = new SurveyPage();
		addPage(surveyPage);
	}

	@Override
	public boolean performFinish() {
		surveyAnswers = surveyPage.getSurveyResults();
		String email = (String) surveyAnswers.get("email");
		surveyAnswers.remove("email");

		this.email = getRandomEmailIfAbsent(email);
		this.onid = (String) surveyAnswers.get("onid");
		surveyAnswers.remove("onid");

		return true;
	}

	@Override
	public boolean performCancel() {
		surveyAnswers = new JSONObject();
		this.email = getRandomEmailIfAbsent(null);

		return true;
	}

	private String getRandomEmailIfAbsent(String email) {
		if (email == null || email.trim().isEmpty())
			return new BigInteger(96, new Random()).toString(32);
		else
			return email.trim();
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.eclipse.ui.handlers.SurveyProvider#getSurveyResults()
	 */
	@Override
	public String getSurveyResults() {
		return surveyAnswers.toJSONString();
	}

	/* (non-Javadoc)
	 * @see edu.oregonstate.cope.eclipse.ui.handlers.SurveyProvider#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getOnid() {
		return onid;
	}
}
