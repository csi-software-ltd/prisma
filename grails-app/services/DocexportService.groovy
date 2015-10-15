import groovy.xml.MarkupBuilder
class DocexportService {

  String getBankanketaXML(_data){
    def oWriter = new StringWriter()
    def xml = new MarkupBuilder(oWriter)
    xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
    xml.anketa() {
      licenses() {
        _data.complicenses.each { clic ->
          license{
            name(clic.name)
            ldate(String.format('%td.%<tm.%<tY',clic.ldate))
            authority(clic.authority)
          }
        }
      }
      founders() {
        _data.compfounders.each { compfounder ->
          founder{
            name(compfounder.shortname?:compfounder.company_name?:' ')
            passport(compfounder.passport?:compfounder.legaladr?:' ')
            summa(compfounder.summa/1000)
            share(compfounder.share)
          }
        }
      }
    }
    oWriter.toString()
  }
}