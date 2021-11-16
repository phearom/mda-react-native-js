import Foundation
import IPificationSDK

@objc(RNAuthenticationService) class RNAuthenticationService: NSObject {
  @objc static func requiresMainQueueSetup() -> Bool {return false}
  
  @objc func unregisterNetwork(){

  }
  @objc func doAuthorizationNoParam(_ callback:  @escaping RCTResponseSenderBlock) {

   do{
      let authorizationService = AuthorizationService()
       authorizationService.callbackFailed = { (error) -> Void in
        callback([error.localizedDescription, NSNull()])
       }
       authorizationService.callbackSuccess = { (response) -> Void in
            callback([NSNull(), response.getCode()])
       }
       try authorizationService.doAuthorization()
   
   } catch{
       print("Unexpected error: \(error).")
       callback([error.localizedDescription, NSNull()])
   }
  }
  
  @objc func setAuthorizationServiceConfiguration(_ name: String){
      // do nothing, just sync with Android function
  }
  @objc func doAuthorization(_ params: NSDictionary, cb callback:  @escaping RCTResponseSenderBlock) {

   do{
       guard let infoDictionary = params as? [String: Any] else {
          return
       }
       let keys = params.allKeys as? [String]
    //    print(keys)
       let authorizationService = AuthorizationService()
       let builder =  AuthorizationRequest.Builder()
       
       
       if(keys != nil){
           for key in keys! {
               if(key != "state" && key != "state" && key != "scope"){
                    builder.addQueryParam(key: key, value: infoDictionary[key] as! String)
               }
           }
       }
       
       
       if(infoDictionary.index(forKey: "state") != nil){
            builder.setState(value: infoDictionary["state"] as! String)
       }
       if(infoDictionary.index(forKey: "scope") != nil){
            builder.setScope(value: infoDictionary["scope"] as! String)
       }

      
       authorizationService.callbackFailed = { (error) -> Void in
            callback([error.localizedDescription, NSNull()])
       }
       authorizationService.callbackSuccess = { (response) -> Void in
        callback([NSNull(), response.getCode()])
       }
       try authorizationService.doAuthorization(builder.build())
   
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
