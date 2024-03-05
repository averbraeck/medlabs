package nl.tudelft.simulation.medlabs.simulation.gui.chart;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CSVExporter;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.chartpart.Chart;

/**
 * MenuXChartPanel is an extension of the XChartPanel from org.knowm.xchart
 * where the pop-up menu has been made more accessible, amongst others to
 * maximize the view of a chart.
 * <p>
 * Copyright (c) 2022-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> the type of chart
 */
public class MenuXChartPanel<T extends Chart<?, ?>> extends XChartPanel<T> implements EventListener {
	/** */
	private static final long serialVersionUID = 20220918L;

	/** the dynamic chart for which this is the panel. */
	private final DynamicChart<T> dynamicChart;

	/**
	 * Create a chart from XChart in a panel that allows for a more extended menu on
	 * right-click.
	 * 
	 * @param dynamicChart T; the chart to display in a panel
	 */
	public MenuXChartPanel(final DynamicChart<T> dynamicChart) {
		super(dynamicChart.getChart());
		this.dynamicChart = dynamicChart;

		// remove the existing mouse listener for the popup menu
		MouseListener[] mls = getListeners(MouseListener.class);
		for (MouseListener ml : mls) {
			if (ml.getClass().getSimpleName().contains("PopUpMenuClickListener"))
				removeMouseListener(ml);
		}

		// add a new, extensible, mouse listener that also includes the XChartPanel menu
		// options
		MouseListener menuListener = new PopUpMenuClickListener();
		this.addMouseListener(menuListener);

		try {
			dynamicChart.addListener(this, DynamicChart.UPDATE_EVENT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new maximized window.
	 */
	private void maximizeWindow() {
		JFrame frame = new JFrame(getChart().getTitle());
		frame.setPreferredSize(new Dimension(1920, 1080));
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(new MenuXChartPanel<T>(this.dynamicChart), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

	/** {@inheritDoc} */
	@Override
	public void notify(final Event event) throws RemoteException {
		if (event.getType().equals(DynamicChart.UPDATE_EVENT)) {
			this.repaint();
		}
	}

	private void showPrintDialog() {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		if (printJob.printDialog()) {
			try {
				// Page format
				PageFormat pageFormat = printJob.defaultPage();
				Paper paper = pageFormat.getPaper();
				if (this.getWidth() > this.getHeight()) {
					pageFormat.setOrientation(PageFormat.LANDSCAPE);
					paper.setImageableArea(0, 0, pageFormat.getHeight(), pageFormat.getWidth());
				} else {
					paper.setImageableArea(0, 0, pageFormat.getWidth(), pageFormat.getHeight());
				}
				pageFormat.setPaper(paper);
				pageFormat = printJob.validatePage(pageFormat);

				String jobName = "XChart " + getChart().getTitle().trim();
				printJob.setJobName(jobName);

				printJob.setPrintable(new Printer(this), pageFormat);
				printJob.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}

	private void showSaveAsDialog() {
		UIManager.put("FileChooser.saveButtonText", "Save");
		UIManager.put("FileChooser.fileNameLabelText", "File Name:");
		JFileChooser fileChooser = new JFileChooser();
		FileFilter pngFileFilter = new SuffixSaveFilter("png"); // default
		fileChooser.addChoosableFileFilter(pngFileFilter);
		fileChooser.addChoosableFileFilter(new SuffixSaveFilter("jpg"));
		fileChooser.addChoosableFileFilter(new SuffixSaveFilter("bmp"));
		fileChooser.addChoosableFileFilter(new SuffixSaveFilter("gif"));

		// VectorGraphics2D is optional, so if it's on the classpath, allow saving
		// charts as vector
		// graphic
		try {
			Class.forName("de.erichseifert.vectorgraphics2d.VectorGraphics2D");
			// it exists on the classpath
			fileChooser.addChoosableFileFilter(new SuffixSaveFilter("svg"));
			fileChooser.addChoosableFileFilter(new SuffixSaveFilter("eps"));
		} catch (ClassNotFoundException e) {
			// it does not exist on the classpath
		}
		try {
			Class.forName("de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D");
			// it exists on the classpath
			fileChooser.addChoosableFileFilter(new SuffixSaveFilter("pdf"));
		} catch (ClassNotFoundException e) {
			// it does not exist on the classpath
		}

		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setFileFilter(pngFileFilter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			if (fileChooser.getSelectedFile() != null) {
				File theFileToSave = fileChooser.getSelectedFile();
				try {
					if (fileChooser.getFileFilter() == null) {
						BitmapEncoder.saveBitmap(getChart(), theFileToSave.getCanonicalPath(), BitmapFormat.PNG);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.jpg,*.JPG")) {
						BitmapEncoder.saveJPGWithQuality(getChart(),
								BitmapEncoder.addFileExtension(theFileToSave.getCanonicalPath(), BitmapFormat.JPG),
								1.0f);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.png,*.PNG")) {
						BitmapEncoder.saveBitmap(getChart(), theFileToSave.getCanonicalPath(), BitmapFormat.PNG);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.bmp,*.BMP")) {
						BitmapEncoder.saveBitmap(getChart(), theFileToSave.getCanonicalPath(), BitmapFormat.BMP);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.gif,*.GIF")) {
						BitmapEncoder.saveBitmap(getChart(), theFileToSave.getCanonicalPath(), BitmapFormat.GIF);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.svg,*.SVG")) {
						VectorGraphicsEncoder.saveVectorGraphic(getChart(), theFileToSave.getCanonicalPath(),
								VectorGraphicsFormat.SVG);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.eps,*.EPS")) {
						VectorGraphicsEncoder.saveVectorGraphic(getChart(), theFileToSave.getCanonicalPath(),
								VectorGraphicsFormat.EPS);
					} else if (fileChooser.getFileFilter().getDescription().equals("*.pdf,*.PDF")) {
						VectorGraphicsEncoder.saveVectorGraphic(getChart(), theFileToSave.getCanonicalPath(),
								VectorGraphicsFormat.PDF);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void showExportAsDialog() {
		UIManager.put("FileChooser.saveButtonText", "Export");
		UIManager.put("FileChooser.fileNameLabelText", "Export To:");
		UIManager.put("FileChooser.fileNameLabelMnemonic", "Export To:");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		disableLabel(fileChooser.getComponents());
		disableTextField(fileChooser.getComponents());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(final File f) {

				return f.isDirectory();
			}

			@Override
			public String getDescription() {

				return "Any Directory";
			}
		});
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setDialogTitle("Export");

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			File theFileToSave = null;
			if (fileChooser.getSelectedFile() != null) {
				if (fileChooser.getSelectedFile().exists()) {
					theFileToSave = fileChooser.getSelectedFile();
				} else {
					theFileToSave = new File(fileChooser.getSelectedFile().getParent());
				}
			}

			try {
				CSVExporter.writeCSVColumns((XYChart) getChart(),
						theFileToSave.getCanonicalPath() + File.separatorChar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void disableTextField(final Component[] comp) {
		for (Component component : comp) {
			// System.out.println(component.toString());
			if (component instanceof JPanel) {
				disableTextField(((JPanel) component).getComponents());
			} else if (component instanceof JTextField) {
				component.setVisible(false);
				return;
			}
		}
	}

	private void disableLabel(final Component[] comp) {
		for (Component component : comp) {
			// System.out.println(comp[x].toString());
			if (component instanceof JPanel) {
				disableLabel(((JPanel) component).getComponents());
			} else if (component instanceof JLabel) {
				// System.out.println(comp[x].toString());
				component.setVisible(false);
				return;
			}
		}
	}

	protected class PopUpMenuClickListener extends MouseAdapter {
		/** {@inheritDoc} */
		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				doPop(e);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				doPop(e);
			}
		}

		private void doPop(final MouseEvent e) {
			XChartPanelPopupMenu menu = new XChartPanelPopupMenu();
			menu.show(e.getComponent(), e.getX(), e.getY());
			menu.getGraphics().dispose();
		}
	}

	protected class XChartPanelPopupMenu extends JPopupMenu {
		/** */
		private static final long serialVersionUID = 20220918L;

		/**
		 * Create a pop-up menu to save-as, export-to, print, and maximize the window.
		 */
		@SuppressWarnings("checkstyle:all")
		public XChartPanelPopupMenu() {
			JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
			saveAsMenuItem.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					showSaveAsDialog();
				}

				// @formatter:off
                @Override public void mousePressed(final MouseEvent e) { }
                @Override public void mouseExited(final MouseEvent e) { }
                @Override public void mouseEntered(final MouseEvent e) { }
                @Override public void mouseClicked(final MouseEvent e) { }
                // @formatter:on
			});
			add(saveAsMenuItem);

			JMenuItem printMenuItem = new JMenuItem("Print...");
			printMenuItem.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					showPrintDialog();
				}

				// @formatter:off
                @Override public void mousePressed(final MouseEvent e) { }
                @Override public void mouseExited(final MouseEvent e) { }
                @Override public void mouseEntered(final MouseEvent e) { }
                @Override public void mouseClicked(final MouseEvent e) { }
                // @formatter:on
			});
			add(printMenuItem);

			if (getChart() instanceof XYChart) {
				JMenuItem exportAsMenuItem = new JMenuItem("Export To...");
				exportAsMenuItem.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(final MouseEvent e) {
						showExportAsDialog();
					}

					// @formatter:off
                    @Override public void mousePressed(final MouseEvent e) { }
                    @Override public void mouseExited(final MouseEvent e) { }
                    @Override public void mouseEntered(final MouseEvent e) { }
                    @Override public void mouseClicked(final MouseEvent e) { }
                    // @formatter:on
				});
				add(exportAsMenuItem);
			}

			JMenuItem maximizeMenuItem = new JMenuItem("Maximize");
			maximizeMenuItem.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					maximizeWindow();
				}

				// @formatter:off
                @Override public void mousePressed(final MouseEvent e) { }
                @Override public void mouseExited(final MouseEvent e) { }
                @Override public void mouseEntered(final MouseEvent e) { }
                @Override public void mouseClicked(final MouseEvent e) { }
                // @formatter:on
			});
			add(maximizeMenuItem);

		}
	}

	/**
	 * File filter based on the suffix of a file. This file filter accepts all files
	 * that end with .suffix or the capitalized suffix.
	 * 
	 * @author Benedikt Bünz
	 */
	private static class SuffixSaveFilter extends FileFilter {
		/** the suffix (or capitalized version) to look for. */
		private final String suffix;

		/**
		 * This file filter accepts all files that end with .suffix or the capitalized
		 * suffix.
		 * 
		 * @param suffix String; the suffix (or capitalized version) to look for
		 */
		SuffixSaveFilter(final String suffix) {
			this.suffix = suffix;
		}

		/** {@inheritDoc} */
		@Override
		public boolean accept(final File f) {
			if (f.isDirectory()) {
				return true;
			}
			String s = f.getName();
			return s.endsWith("." + this.suffix) || s.endsWith("." + this.suffix.toUpperCase());
		}

		/** {@inheritDoc} */
		@Override
		public String getDescription() {
			return "*." + this.suffix + ",*." + this.suffix.toUpperCase();
		}
	}

	/** A printer object to print a graphics component. */
	public static class Printer implements Printable {
		/** the component to print. */
		private final Component component;

		/**
		 * Create a printer object to print the contents of a component.
		 * 
		 * @param c Component; he component to print
		 */
		Printer(final Component c) {
			this.component = c;
		}

		/** {@inheritDoc} */
		@Override
		public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) {
			if (pageIndex > 0) {
				return NO_SUCH_PAGE;
			}
			Graphics2D g2 = (Graphics2D) graphics;
			g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			double sx = pageFormat.getImageableWidth() / this.component.getWidth();
			double sy = pageFormat.getImageableHeight() / this.component.getHeight();
			g2.scale(sx, sy);
			this.component.printAll(g2);
			return PAGE_EXISTS;
		}
	}

}
