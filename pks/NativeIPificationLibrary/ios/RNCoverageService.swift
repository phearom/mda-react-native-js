import Foundation
import IPificationSDK

@objc(RNCoverageService) class RNCoverageService: NSObject {
  @objc static func requiresMainQueueSetup() -> Bool {return false}
  
  @objc func unregisterNetwork(){

  }

  @objc func setAuthorizationServiceConfiguration(_ name: String){
      // do nothing, just sync with Android function
  }

  @objc func checkCoverage(_ callback:  @escaping RCTResponseSenderBlock) {

   
        let coverageService = CoverageService()
        coverageService.callbackFailed = { (error) -> Void in
    //          print(error.localizedDescription)
            callback([error.localizedDescription, NSNull()])
        }
        coverageService.callbackSuccess = { (response) -> Void in
    //           print("check coverage result: ", response.isAvailable())
            callback([NSNull(), response.isAvailable(), response.getOperatorCode])
        }
        
        coverageService.checkCoverage()
  }

  @objc func checkCoverageWithPhoneNumber(_ params: NSDictionary, cb  callback:  @escaping RCTResponseSenderBlock) {
        var phoneNumber = ""
        do {
            guard let infoDictionary = params as? [String: Any] else {
                return
            }
            if(infoDictionary.index(forKey: "phoneNumber") != nil){
                    phoneNumber = infoDictionary["phoneNumber"] as! String
            }
            let coverageService = CoverageService()
            coverageService.callbackFailed = { (error) -> Void in
        //          print(error.localizedDescription)
                    callback([error.localizedDescription, NSNull()])
            }
            coverageService.callbackSuccess = { (response) -> Void in
        //           print("check coverage result: ", response.isAvailable())
                    callback([NSNull(), response.isAvailable(), response.getOperatorCode()])
            }
            try coverageService.checkCoverage(phoneNumber: phoneNumber)
        } catch{
            print("Unexpected error: \(error).")
            callback([error.localizedDescription, NSNull()])
        }
  }

  @objc func getConfigurationByName(_ name: String, resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock)-> Void{  
       var result  = infoForKey(name.uppercased())
       resolve(result)
   }
    func infoForKey(_ key: String) -> String? {
        var result = (Bundle.main.infoDictionary?[key] as? String)?
            .replacingOccurrences(of: "\\", with: "")

            return result ?? key
    }
}
