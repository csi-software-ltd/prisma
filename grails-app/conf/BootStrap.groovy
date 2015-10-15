class BootStrap {
	def transactionManager
    def init = { servletContext ->
    	transactionManager.setNestedTransactionAllowed(true)
    }
    def destroy = {
    }
}