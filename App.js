/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */


import React, { useState, useRef, useEffect } from "react";

import {
  SafeAreaView,
  StyleSheet,
  View,
  StatusBar,
  TouchableOpacity,
  Text,
  NativeModules,
} from "react-native";
const { RNCoverageService, RNAuthenticationService } = NativeModules;
import { Platform } from "react-native";
import { Colors } from "react-native/Libraries/NewAppScreen";
import PhoneInput from "react-native-phone-number-input";
import uuid from "react-native-uuid";

import jwt_decode from "jwt-decode";
import md5 from "md5";

const App = () => {
  const RESULT_URL = "http://apitest.mekongsms.com/getresult.aspx";
  const secret = "fff17577d653dced70e9b4aa070f3f...";

  const [phoneNumber, setPhoneNumnber] = useState("10523296");
  const [token, setToken] = useState("");
  const [formattedValue, setFormattedValue] = useState("85510523296");
  const [coverageResult, setCoverageResult] = useState(false);
  const [authorizationResult, setAuthorizationResult] = useState();
  const [disabled, setDisabled] = useState(false);
  const [showMessage, setShowMessage] = useState(false);
  const [phoneVerified, setPhoneVerified] = useState(false);
  const [mobileId, setMobileId] = useState("");

  const phoneInput = useRef<PhoneInput>(null);

  useEffect(() => {
    return () => {
      if (Platform.OS === "android") {
        console.log("componentWillUnmount android");
        RNCoverageService.unregisterNetwork();
      }
    };
  }, []);
  checkCoverage = () => {
    console.log("checkCoverage");
    RNCoverageService.setAuthorizationServiceConfiguration(
      "ipification-services.json"
    );
    RNCoverageService.checkCoverage((error, isAvailable) => {
      console.log(" isAvailable ", isAvailable, error);
      if (isAvailable) {
        setCoverageResult(isAvailable);
        doAuthentication();
      } else {
        setCoverageResult(isAvailable || error);
        setDisabled(false);
      }
    });
  };

  doAuthentication = async () => {
    RNAuthenticationService.setAuthorizationServiceConfiguration(
      "ipification-services.json"
    );

    //var state = getRandomValues();
    console.log("doAuthentication", state);
    var clientId = await RNAuthenticationService.getConfigurationByName(
      "client_id"
    );

    var phone = formattedValue.replace("+", "");
    var trxId = uuid.v4();
    var redirectUrl = await RNAuthenticationService.getConfigurationByName(
      "redirect_uri"
    );
    var state = clientId + secret + phone + trxId + redirectUrl;
    console.log(state);
    console.log(md5(state));
    RNAuthenticationService.doAuthorization(
      {
        login_hint: phone,
        state: md5(state),
        scope: "openid ip:phone_verify ip:mobile_id",
        trx_id: trxId,
      },
      (error, code) => {
        console.log(error, code);
        if (code != null) {
          setAuthorizationResult(code);
          doExchangeCode(trxId);
        } else {
          setAuthorizationResult(error);
        }
        //setDisabled(false);
      }
    );
  };
  doExchangeCode = async (trxId) => {
    console.log(authorizationResult);
    //return;

    var client_id = await RNAuthenticationService.getConfigurationByName(
      "client_id"
    );
    var redirect_uri = await RNAuthenticationService.getConfigurationByName(
      "redirect_uri"
    );
    console.log("client_id,", client_id);
    var details = {
      client_id: client_id,
      secret: secret,
      trx_id: trxId,
      code: authorizationResult,
    };
    var formBody = [];
    for (var property in details) {
      var encodedKey = encodeURIComponent(property);
      var encodedValue = encodeURIComponent(details[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody = formBody.join("&");
    console.log(formBody);
    fetch(RESULT_URL, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: formBody,
    })
      .then((response) => response.json())
      .then((responseJson) => {
        console.log(responseJson);

        //success
        if (responseJson["error"] == "0") {
          var pf = JSON.parse(responseJson["phone_verified"].toLowerCase());
          setPhoneVerified(pf);
          setMobileId(responseJson["mobile_id"]);
          console.log(responseJson["phone_verified"]);
          console.log(responseJson["mobile_id"]);
        } else {
          setPhoneVerified(false);
          console.log(responseJson["error_description"]);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  };

  return (
    <View style={styles.container}>
      <SafeAreaView style={styles.wrapper}>
        {showMessage && (
          <View style={styles.message}>
            <Text>Phone Number : {formattedValue}</Text>
            <Text>
              Supported Telco :{" "}
              {coverageResult == true
                ? "true"
                : coverageResult == false || coverageResult == null
                ? "false"
                : coverageResult}
            </Text>
            <Text>Authentication Result : {authorizationResult}</Text>
            <Text>
              Phone Verified : {phoneVerified == true ? "true" : "false"}
            </Text>
            <Text>Mobile ID : {mobileId}</Text>
          </View>
        )}
        <PhoneInput
          ref={phoneInput}
          defaultValue={phoneNumber}
          defaultCode="KH"
          layout="first"
          onChangeText={(text) => {
            setPhoneNumnber(text);
          }}
          onChangeFormattedText={(text) => {
            setFormattedValue(text);
            // setCountryCode(phoneInput.current?.getCountryCode() || '');
          }}
          countryPickerProps={{ withAlphaFilter: true }}
          disabled={disabled}
          withDarkTheme
          withShadow
          autoFocus
        />
        <TouchableOpacity
          style={styles.button}
          onPress={() => {
            const checkValid = phoneInput.current?.isValidNumber(phoneNumber);
            console.log("Valid Phone : " + checkValid);
            // setDisabled(true)
            setShowMessage(true);
            checkCoverage();
          }}
        >
          <Text style={styles.buttonText}>Authorize</Text>
        </TouchableOpacity>
      </SafeAreaView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.lighter,
  },
  wrapper: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  button: {
    marginTop: 20,
    height: 50,
    width: 300,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#7CDB8A",
    shadowColor: "rgba(0,0,0,0.4)",
    shadowOffset: {
      width: 1,
      height: 5,
    },
    shadowOpacity: 0.34,
    shadowRadius: 6.27,
    elevation: 10,
  },
  buttonText: {
    color: "white",
    fontSize: 14,
  },
  redColor: {
    backgroundColor: "#F57777",
  },
  message: {
    borderWidth: 1,
    borderRadius: 5,
    padding: 20,
    marginBottom: 20,
    justifyContent: "center",
    alignItems: "flex-start",
  },
});

export default App;
