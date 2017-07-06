package manager.composite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;
import manager.SWTResourceManager;
import manager.dialog.LetterDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class LetterComposite extends Composite {
	private Table table;
	@SuppressWarnings("unused")
	private LetterComposite lc;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LetterComposite(final Composite parent, int style) {
		super(parent, style);
		lc = this;
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clear();
				reload();
			}
		});
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.heightHint = 35;
		gd_btnNewButton.widthHint = 150;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setText("\uC0C8\uB85C\uACE0\uCE68");
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
				try {
					TableItem item = table.getItem( table.getSelectionIndex() );
					if(item == null)
						return;

					LinAllManager.getInstance();
					LetterDialog dialog = new LetterDialog(LinAllManager.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
					dialog.open(item);
				} catch (Exception e2) { }
			}
		});
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setBackground(SWTResourceManager.getColor(51, 51, 51));
		table.setForeground(SWTResourceManager.getColor(255, 255, 255));
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(40);
		tblclmnNewColumn.setText("\uBC88\uD638");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(80);
		tblclmnNewColumn_1.setText("\uBCF4\uB0B8\uC774");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(150);
		tblclmnNewColumn_2.setText("\uC81C\uC625");
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(80);
		tblclmnNewColumn_4.setText("\uBCF4\uB0B8\uB0A0\uC790");

		//
		reload();
	}
	
	public void delete(TableItem item) {
		//
		String[] letter_data = (String[])item.getData();
		//
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete from letter where item_object_id = ?");
			pstm.setInt(1, Integer.parseInt(letter_data[0]));
			pstm.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {		
			SQLUtil.close(rs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void clear() {
		table.removeAll();
	}

	public void reload() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from letter where receiver in ('메티스', '미소피아', '운영자') ORDER BY item_object_id DESC");
			rs = pstm.executeQuery();
			
			while (rs.next()) {
				String[] letter = new String[6];
				letter[0] = String.valueOf(rs.getInt("item_object_id"));
				letter[1] = rs.getString("sender");
				letter[2] = rs.getString("subject");
				letter[3] = rs.getString("date");
				letter[4] = rs.getString("content");
				letter[5] = rs.getString("receiver");
				
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setData(letter);
				tableItem.setText(letter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
