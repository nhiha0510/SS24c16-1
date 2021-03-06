package vn.com.cowmanager.model.report;

import java.util.Collection;
import java.util.Map;

import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.cowmanager.model.arrangement.CageManager;
import vn.com.cowmanager.model.feeding.Storage;

@DClass(schema = "report", serialisable = false)
public class CheckExpReport {
	private static Integer idCounter = 0;

	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private Integer id;

	@DAttr(name = "dateLeftForChecking", type = Type.Integer, length = 25, optional = false)
	private Integer dateLeftForChecking;

	@DAttr(name = "storages", type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = Storage.class, attributes = {
			Storage.A_id, Storage.A_product, Storage.A_cost, Storage.A_quantity, Storage.A_provider, Storage.A_dateLeft,
			Storage.A_productStatus, Storage.A_checkExpReport }))
	@DAssoc(ascName = "checkExpReport-has-storage", role = "checkExpReport", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = CageManager.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<Storage> storages;

	@DAttr(name = "numberOfResult", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private Integer numberOfResult;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public CheckExpReport(@AttrRef("dateLeftForChecking") Integer dateLeftForChecking)
			throws NotPossibleException, DataSourceException {
		this.id = ++idCounter;
		this.dateLeftForChecking = dateLeftForChecking;
		doQueryReport();
	}

	private void doQueryReport() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		// create a query to look up Student from the data source
		// and then populate the output attribute (students) with the result
		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query
		// parameter value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, Storage.class, new String[] { Storage.A_dateLeft },
				new Op[] { Op.LT }, new Object[] { dateLeftForChecking });

		Map<Oid, Storage> result = qrm.getDom().retrieveObjects(Storage.class, q);

		if (result != null) {
			// update the main output data
			storages = result.values();

			// update other output (if any)
			numberOfResult = storages.size();
		} else {
			// no data found: reset output
			resetOutput();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		storages = null;
		numberOfResult = 0;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addCageManager(Collection<CageManager> cageManagers) {
		// do nothing
		return false;
	}

	public Integer getId() {
		return id;
	}

	public Integer getDateLeftForChecking() {
		return dateLeftForChecking;
	}

	public void setDateLeftForChecking(Integer dateLeftForChecking) throws NotPossibleException, DataSourceException {

		boolean doQueryReport = (dateLeftForChecking != null && !dateLeftForChecking.equals(this.dateLeftForChecking));

		this.dateLeftForChecking = dateLeftForChecking;

		if (doQueryReport) {
			doQueryReport();
		}
	}

	public void setDateStatus(String dateStatus) {

	}

	public Integer getNumberOfResult() {
		return numberOfResult;
	}

	public Collection<Storage> getStorages() {
		return storages;
	}

	public void setStorages(Collection<Storage> storages) {
		this.storages = storages;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Report(" + id + dateLeftForChecking + ((storages != null) ? "Ok" : "Null") + ")";
	}
}
