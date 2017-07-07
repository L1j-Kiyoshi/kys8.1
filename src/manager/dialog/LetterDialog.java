package manager.dialog;

import java.sql.Timestamp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import l1j.server.server.datatables.LetterTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import manager.LinAllManager;

public class LetterDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	public static Display display;
	private Text text_3;
	private Text text_4;
	private Text txtRe;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LetterDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(TableItem item) {
		createContents(item);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(final TableItem item) {
		//
		final String[] letter_data = (String[])item.getData();
		//
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 473);
		shell.setText("\uD3B8\uC9C0\uBCF4\uAE30");
		//画面の中央に
		display = Display.getDefault();
		shell.setBounds((display.getBounds().width / 2) - (shell.getBounds().width / 2),
				(display.getBounds().height / 2) - (shell.getBounds().height / 2),
				shell.getBounds().width, shell.getBounds().height);
		shell.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setText("\uBCF4\uB0B8\uC774");
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setEditable(false);
		text_1.setText(letter_data[1]);
		
		Label lblNewLabel_3 = new Label(shell, SWT.NONE);
		lblNewLabel_3.setText("\uBC1B\uB294\uC774");
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.setEditable(false);
		text_2.setText(letter_data[5]);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("\uC81C\uBAA9");
		
		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setEditable(false);
		text.setText(letter_data[2]);
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setText("\uB0B4\uC6A9");
		
		StyledText styledText = new StyledText(shell, SWT.BORDER);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		styledText.setEditable(false);
		styledText.setText(letter_data[4]);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
			    messageBox.setMessage("本当に削除しますか？");
			    if(messageBox.open() == SWT.YES) {
					// 削除する。
			    	LinAllManager.getInstance().getLetterComposite().delete(item);
					//
					close();
			    }
			}
		});
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton.setText("\uC0AD\uC81C");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton_1.setText("\uD655\uC778");
		new Label(shell, SWT.NONE);
		
		CLabel lblBy = new CLabel(shell, SWT.NONE);
		lblBy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBy.setText("\uB9B0\uC62C\uC18C\uD504\uD2B8");
		
		Label lblNewLabel_4 = new Label(shell, SWT.NONE);
		lblNewLabel_4.setText("\uBCF4\uB0B8\uC774");
		
		text_3 = new Text(shell, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_3.setText("\uBA54\uD2F0\uC2A4");
		
		Label lblNewLabel_5 = new Label(shell, SWT.NONE);
		lblNewLabel_5.setText("\uBC1B\uB294\uC774");
		
		text_4 = new Text(shell, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setText(letter_data[1]);
		
		Label lblNewLabel_6 = new Label(shell, SWT.NONE);
		lblNewLabel_6.setText("\uC81C\uBAA9");
		
		txtRe = new Text(shell, SWT.BORDER);
		txtRe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRe.setText("Re: " + letter_data[2]);
		
		Label lblNewLabel_7 = new Label(shell, SWT.NONE);
		lblNewLabel_7.setText("\uB0B4\uC6A9");
		
		final StyledText styledText2 = new StyledText(shell, SWT.BORDER);
		styledText2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		new Label(shell, SWT.NONE);
		
		Button btnNewButton_2 = new Button(shell, SWT.NONE);
		GridData gd_btnNewButton_2 = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
		gd_btnNewButton_2.widthHint = 150;
		btnNewButton_2.setLayoutData(gd_btnNewButton_2);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION |SWT.YES | SWT.NO);
			    messageBox.setMessage("手紙を発送しますか？");
			    if(messageBox.open() == SWT.YES) {
			    	//
			    	L1PcInstance receiver = L1World.getInstance().getPlayer(text_4.getText());
					if (receiver != null) {
						WritePrivateMail(receiver, text_3.getText(), txtRe.getText(), styledText2.getText());				
					} else {
						WritePrivateMail(text_4.getText(), text_3.getText(), txtRe.getText(), styledText2.getText());				
					}
					//
					close();
			    }
			}
		});
		btnNewButton_2.setText("\uBCF4\uB0B4\uAE30");
	}
	
	private void close() {
		// クリーン。
		LinAllManager.getInstance().getLetterComposite().clear();
		// リロード。
		LinAllManager.getInstance().getLetterComposite().reload();
		//
		shell.dispose();
	}
	
	private void WritePrivateMail(String receiver, String sender, String title, String message) {
		try {
			Timestamp dTime = new Timestamp(System.currentTimeMillis());
			String receiverName = receiver;
			String subject = title;
			String content = message;
			
			LetterTable.getInstance().writeLetter(949, dTime, sender, receiverName, 0, subject, content);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	private void WritePrivateMail(L1PcInstance receiver, String sender, String title, String message) {
		try {
			Timestamp dTime = new Timestamp(System.currentTimeMillis());
			String receiverName = receiver.getName();
			String subject = title;
			String content = message;
			
			LetterTable.getInstance().writeLetter(949, dTime, sender, receiverName, 0, subject, content);			
			sendMessageToReceiver(receiver, 0, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessageToReceiver(L1PcInstance receiver, final int type, final int MAILBOX_SIZE) {
		if (receiver != null && receiver.getOnlineStatus() != 0) {
			LetterList(receiver, type, MAILBOX_SIZE);
			receiver.sendPackets(new S_SkillSound(receiver.getId(), 1091));
			receiver.sendPackets(new S_ServerMessage(428)); //メールが届きました。
		}
	}
	
	private void LetterList(L1PcInstance pc, int type, int count) {
		pc.sendPackets(new S_LetterList(pc, type, count));
	}
	
}
