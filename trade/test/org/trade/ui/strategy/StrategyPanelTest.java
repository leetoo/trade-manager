package org.trade.ui.strategy;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import jsyntaxpane.DefaultSyntaxKit;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.broker.BrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.persistent.PersistentModel;
import org.trade.persistent.dao.Candle;
import org.trade.persistent.dao.Rule;
import org.trade.persistent.dao.Strategy;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.TradestrategyTest;
import org.trade.strategy.StrategyRule;
import org.trade.ui.TradeAppLoadConfig;
import org.trade.ui.base.StreamEditorPane;

/**
 */
public class StrategyPanelTest extends TestCase {

	private final static Logger _log = LoggerFactory
			.getLogger(StrategyPanelTest.class);

	private PersistentModel tradePersistentModel = null;
	private Tradestrategy tradestrategy = null;
	private String m_templateName = null;
	private String m_strategyDir = null;
	private String m_tmpDir = "temp";

	/**
	 * Method setUp.
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		try {
			TradeAppLoadConfig.loadAppProperties();
			m_templateName = ConfigProperties
					.getPropAsString("trade.strategy.template");
			assertNotNull(m_templateName);
			m_strategyDir = ConfigProperties
					.getPropAsString("trade.strategy.default.dir");
			assertNotNull(m_strategyDir);
			this.tradePersistentModel = (PersistentModel) ClassFactory
					.getServiceForInterface(PersistentModel._persistentModel,
							this);
			this.tradestrategy = TradestrategyTest.getTestTradestrategy();
			List<Strategy> strategies = this.tradePersistentModel
					.findStrategies();
			for (Strategy strategy : strategies) {
				if (null != strategy.getClassName()) {
					String fileName = m_strategyDir + "/"
							+ StrategyRule.PACKAGE.replace('.', '/')
							+ strategy.getClassName() + ".java";

					String content = readFile(fileName);
					if (strategy.getRules().isEmpty()) {
						Rule nextRule = new Rule(strategy, 1, null, new Date(),
								content.getBytes(), new Date());
						strategy.add(nextRule);
						this.tradePersistentModel.persistRule(nextRule);
					}
				}
			}
		} catch (Exception ex) {
			_log.error("Error getting Yahoo data msg: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Method tearDown.
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		File dir = new File(m_tmpDir);
		StrategyPanel.deleteDir(dir);
		TradestrategyTest.removeTestTradestrategy();
	}

	@Test
	public void testJEditorPaneTextEquals() {

		try {
			DefaultSyntaxKit.initKit();
			JEditorPane sourceText = new JEditorPane();
			JScrollPane jScrollPane = new JScrollPane(sourceText);
			jScrollPane.setEnabled(true);
			sourceText.setContentType("text/java");
			sourceText.setFont(new Font("monospaced", Font.PLAIN, 12));
			sourceText.setBackground(Color.white);
			sourceText.setForeground(Color.black);
			sourceText.setSelectedTextColor(Color.black);
			sourceText.setSelectionColor(Color.red);
			sourceText.setEditable(true);

			String fileName = m_strategyDir + "/"
					+ StrategyRule.PACKAGE.replace('.', '/') + m_templateName
					+ ".java";
			String content = readFile(fileName);
			sourceText.setText(content);
			assertEquals(content, sourceText.getText());
			writeFile(fileName, content);
			String content1 = readFile(fileName);
			sourceText.setText(null);
			sourceText.setText(content1);
			assertEquals(content1, sourceText.getText());

		} catch (Exception ex) {
			fail("Error creating instance : " + ex.getMessage());
		}
	}

	/**
	 * Method readFile.
	 * @param fileName String
	 * @return String
	 * @throws IOException
	 */
	private String readFile(String fileName) throws IOException {

		FileReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		inputStreamReader = new FileReader(fileName);
		bufferedReader = null;
		bufferedReader = new BufferedReader(inputStreamReader);
		String newLine = "\n";
		StringBuffer sb = new StringBuffer();
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line + newLine);
		}

		if (null != bufferedReader)
			bufferedReader.close();

		if (null != inputStreamReader)
			inputStreamReader.close();
		return sb.toString();
	}

	/**
	 * Method writeFile.
	 * @param fileName String
	 * @param content String
	 * @throws IOException
	 */
	private void writeFile(String fileName, String content) throws IOException {

		OutputStream out = new FileOutputStream(fileName);
		out.write(content.getBytes());
		out.flush();
		out.close();
	}

	@Test
	public void testDoCompileAndRunStrategy() {
		try {
			BrokerModel m_brokerManagerModel = (BrokerModel) ClassFactory
					.getServiceForInterface(BrokerModel._brokerTest, this);

			Vector<Object> parm = new Vector<Object>(0);
			parm.add(m_brokerManagerModel);
			parm.add(this.tradestrategy.getDatasetContainer());
			parm.add(this.tradestrategy.getIdTradeStrategy());
			_log.info("Ready to create Strategy");
			DynamicCode dynacode = new DynamicCode();
			dynacode.addSourceDir(new File(m_strategyDir));
			StrategyRule strategyProxy = (StrategyRule) dynacode
					.newProxyInstance(StrategyRule.class, StrategyRule.PACKAGE
							+ m_templateName, parm);
			_log.info("Created Strategy" + strategyProxy);
			strategyProxy.execute();
			try {
				do {
					Thread.sleep(1000);
				} while (!strategyProxy.isWaiting());

			} catch (InterruptedException e) {
				_log.info(" Thread interupt: " + e.getMessage());
			}

			List<Candle> candles = tradePersistentModel
					.findCandlesByContractAndDateRange(this.tradestrategy
							.getContract().getIdContract(), this.tradestrategy
							.getTradingday().getOpen(), this.tradestrategy
							.getTradingday().getOpen());
			for (Candle candle : candles) {
				double high = candle.getHigh().doubleValue();
				double low = candle.getLow().doubleValue();
				double open = candle.getOpen().doubleValue();
				double close = candle.getClose().doubleValue();
				this.tradestrategy.getDatasetContainer().buildCandle(
						candle.getStartPeriod(), open, high, low, close,
						candle.getVolume(), candle.getVwap().doubleValue(),
						candle.getTradeCount(), 1);
				Thread.sleep(300);
			}
			strategyProxy.cancel();

		} catch (Exception ex) {
			fail("Error creating instance : " + ex.getMessage());
		}
	}

	@Test
	public void testDoCompileRule() {
		File srcDirFile = null;
		try {
			BrokerModel m_brokerManagerModel = (BrokerModel) ClassFactory
					.getServiceForInterface(BrokerModel._brokerTest, this);

			Vector<Object> parm = new Vector<Object>(0);
			parm.add(m_brokerManagerModel);
			parm.add(this.tradestrategy.getDatasetContainer());
			parm.add(this.tradestrategy.getIdTradeStrategy());
			Strategy strategy = this.tradePersistentModel
					.findStrategyById(this.tradestrategy.getStrategy()
							.getIdStrategy());
			Integer version = this.tradePersistentModel
					.findRuleByMaxVersion(strategy);
			Rule myRule = null;
			for (Rule rule : strategy.getRules()) {
				if (version.equals(rule.getVersion()))
					myRule = rule;
			}
			assertNotNull(myRule);
			String fileDir = m_tmpDir + "/"
					+ StrategyRule.PACKAGE.replace('.', '/');
			String className = strategy.getClassName() + ".java";

			srcDirFile = new File(fileDir);
			srcDirFile.mkdirs();
			srcDirFile.deleteOnExit();
			FileWriter fileWriter = new FileWriter(fileDir + className);
			PrintWriter writer = new PrintWriter(fileWriter);
			writer.println(new String(myRule.getRule()));
			writer.flush();
			writer.close();
			fileWriter.close();

			_log.info("Ready to create Strategy");
			DynamicCode dynacode = new DynamicCode();
			dynacode.addSourceDir(new File(m_tmpDir));
			StrategyRule strategyRule = (StrategyRule) dynacode
					.newProxyInstance(StrategyRule.class, StrategyRule.PACKAGE
							+ strategy.getClassName(), parm);
			assertNotNull(strategyRule);

		} catch (Exception ex) {
			fail("Error compiling rule : " + ex.getMessage());
		}
	}

	@Test
	public void testDoCompile() {
		try {
			StrategyPanel strategyPanel = new StrategyPanel(
					this.tradePersistentModel);
			List<Strategy> strategies = this.tradePersistentModel
					.findStrategies();
			assertNotNull("No strategies", strategies);
			assertEquals(false, strategies.isEmpty());

			Strategy strategy = strategies.get(0);
			assertNotNull(strategy);
			Rule myrule = null;

			Collections.sort(strategy.getRules(), Rule.VERSION_ORDER);

			for (Rule rule : strategy.getRules()) {
				myrule = rule;
				break;
			}
			if (null == myrule) {
				myrule = new Rule();
				myrule.setVersion(0);
				myrule.setStrategy(strategy);

			} else {
				myrule.setVersion(myrule.getVersion() + 1);
				myrule.setIdRule(null);
			}

			strategyPanel.doCompile(myrule);
		} catch (Exception ex) {
			fail("Error saving rule : " + ex.getMessage());
		}
	}

	@Test
	public void testDoSave() {
		try {
			StrategyPanel strategyPanel = new StrategyPanel(
					this.tradePersistentModel);
			List<Strategy> strategies = this.tradePersistentModel
					.findStrategies();
			assertNotNull("No strategies", strategies);
			assertEquals(false, strategies.isEmpty());

			Strategy strategy = strategies.get(0);
			assertNotNull(strategy);
			Rule myrule = null;

			Collections.sort(strategy.getRules(), Rule.VERSION_ORDER);

			for (Rule rule : strategy.getRules()) {
				myrule = rule;
			}
			if (null == myrule) {
				myrule = new Rule();
				myrule.setVersion(0);
				myrule.setStrategy(strategy);

			} else {
				myrule.setVersion(myrule.getVersion() + 1);
				myrule.setIdRule(null);
			}
			myrule.setComment("Test Ver: " + myrule.getVersion());
			myrule.setCreateDate(new Date());
			StreamEditorPane textArea = new StreamEditorPane("text/rtf");
			new JScrollPane(textArea);
			String fileDir = m_strategyDir + "/"
					+ StrategyRule.PACKAGE.replace('.', '/');
			String className = strategy.getClassName() + ".java";
			String fileName = fileDir + className;
			String content = strategyPanel.readFile(fileName);
			textArea.setText(content);
			myrule.setRule(textArea.getText().getBytes());
			this.tradePersistentModel.persistRule(myrule);
			Rule ruleSaved = this.tradePersistentModel.findRuleById(myrule
					.getIdRule());

			String javaCode = new String(ruleSaved.getRule());
			assertEquals(javaCode, textArea.getText());
			_log.info("Java file to Saved: " + javaCode);

		} catch (Exception ex) {
			fail("Error saving rule : " + ex.getMessage());
		}
	}
}
