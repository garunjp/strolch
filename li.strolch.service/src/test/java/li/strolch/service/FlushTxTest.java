package li.strolch.service;

import li.strolch.command.AddResourceCommand;
import li.strolch.command.RemoveResourceCommand;
import li.strolch.command.UpdateResourceCommand;
import li.strolch.model.ModelGenerator;
import li.strolch.model.Resource;
import li.strolch.persistence.api.StrolchTransaction;
import li.strolch.service.api.AbstractService;
import li.strolch.service.api.ServiceArgument;
import li.strolch.service.api.ServiceResult;
import li.strolch.service.test.AbstractRealmServiceTest;

import org.junit.Test;

import ch.eitchnet.utils.dbc.DBC;

public class FlushTxTest extends AbstractRealmServiceTest {

	@Test
	public void shouldFlushSuccessfully1() {

		runServiceInAllRealmTypes(FlushingCommandsService1.class, new ServiceArgument());
	}

	@Test
	public void shouldFlushSuccessfully2() {

		runServiceInAllRealmTypes(FlushingCommandsService2.class, new ServiceArgument());
	}

	@Test
	public void shouldRollbackSuccessfully() {

		runServiceInAllRealmTypes(RollbackAfterFlushCommandsService.class, new ServiceArgument());
	}

	public static class FlushingCommandsService1 extends AbstractService<ServiceArgument, ServiceResult> {
		private static final long serialVersionUID = 1L;

		@Override
		protected ServiceResult getResultInstance() {
			return new ServiceResult();
		}

		@Override
		protected ServiceResult internalDoService(ServiceArgument arg) throws Exception {

			String id = "flushSuccessfully";
			Resource resource = ModelGenerator.createResource(id, id, id);

			try (StrolchTransaction tx = openTx(arg.realm)) {

				DBC.PRE.assertNull("Did not expect resource with id " + id, tx.getResourceBy(id, id));

				AddResourceCommand addResCmd = new AddResourceCommand(getContainer(), tx);
				addResCmd.setResource(resource);
				tx.addCommand(addResCmd);
				tx.flush();
				DBC.PRE.assertNotNull("Expected resource with id " + id, tx.getResourceBy(id, id));

				RemoveResourceCommand rmResCmd = new RemoveResourceCommand(getContainer(), tx);
				rmResCmd.setResource(resource);
				tx.addCommand(rmResCmd);
				tx.flush();
				DBC.PRE.assertNull("Expect to remove resource with id " + id, tx.getResourceBy(id, id));

				tx.commitOnClose();
			}

			// now make sure the new resource does not exist
			try (StrolchTransaction tx = openTx(arg.realm)) {

				Resource res = tx.getResourceBy(id, id);
				if (res != null) {
					throw tx.fail("Did not expect resource with id " + id);
				}
			}

			return ServiceResult.success();
		}
	}

	public static class FlushingCommandsService2 extends AbstractService<ServiceArgument, ServiceResult> {
		private static final long serialVersionUID = 1L;

		@Override
		protected ServiceResult getResultInstance() {
			return new ServiceResult();
		}

		@Override
		protected ServiceResult internalDoService(ServiceArgument arg) throws Exception {

			String id = "flushSuccessfully";
			Resource resource = ModelGenerator.createResource(id, id, id);

			try (StrolchTransaction tx = openTx(arg.realm)) {

				DBC.PRE.assertNull("Did not expect resource with id " + id, tx.getResourceBy(id, id));

				AddResourceCommand addResCmd = new AddResourceCommand(getContainer(), tx);
				addResCmd.setResource(resource);
				tx.addCommand(addResCmd);
				tx.flush();
				DBC.PRE.assertNotNull("Expected resource with id " + id, tx.getResourceBy(id, id));

				Resource res = tx.getResourceBy(id, id);

				UpdateResourceCommand updateResCmd = new UpdateResourceCommand(getContainer(), tx);
				updateResCmd.setResource(res);
				tx.addCommand(updateResCmd);

				tx.commitOnClose();
			}

			// now make sure the new resource does exist
			try (StrolchTransaction tx = openTx(arg.realm)) {

				Resource res = tx.getResourceBy(id, id);
				if (res == null) {
					throw tx.fail("Did not find expected resource with id " + id);
				}
			}

			return ServiceResult.success();
		}
	}

	public static class RollbackAfterFlushCommandsService extends AbstractService<ServiceArgument, ServiceResult> {
		private static final long serialVersionUID = 1L;

		@Override
		protected ServiceResult getResultInstance() {
			return new ServiceResult();
		}

		@Override
		protected ServiceResult internalDoService(ServiceArgument arg) throws Exception {

			String id = "flushSuccessfully2";
			Resource resource = ModelGenerator.createResource(id, id, id);

			try (StrolchTransaction tx = openTx(arg.realm)) {

				DBC.PRE.assertNull("Did not expect resource with id " + id, tx.getResourceBy(id, id));

				AddResourceCommand addResCmd = new AddResourceCommand(getContainer(), tx);
				addResCmd.setResource(resource);
				tx.addCommand(addResCmd);
				tx.flush();
				DBC.PRE.assertNotNull("Expected resource with id " + id, tx.getResourceBy(id, id));

				// now force a rollback
				tx.rollbackOnClose();
			}

			// now make sure the new resource does not exist
			try (StrolchTransaction tx = openTx(arg.realm)) {

				Resource res = tx.getResourceBy(id, id);
				if (res != null) {
					throw tx.fail("Did not expect resource with id after rolling back previous TX " + id);
				}
			}

			// now do it over, but use throw
			try (StrolchTransaction tx = openTx(arg.realm)) {

				DBC.PRE.assertNull("Did not expect resource with id " + id, tx.getResourceBy(id, id));

				AddResourceCommand addResCmd = new AddResourceCommand(getContainer(), tx);
				addResCmd.setResource(resource);
				tx.addCommand(addResCmd);
				tx.flush();
				DBC.PRE.assertNotNull("Expected resource with id " + id, tx.getResourceBy(id, id));

				// now force a rollback
				throw tx.fail("Oh snap, something went wrong!");

			} catch (Exception e) {
				// expected
			}

			// now make sure the new resource does not exist
			try (StrolchTransaction tx = openTx(arg.realm)) {

				Resource res = tx.getResourceBy(id, id);
				if (res != null) {
					throw tx.fail("Did not expect resource with id after rolling back previous TX " + id);
				}
			}

			return ServiceResult.success();
		}
	}
}
