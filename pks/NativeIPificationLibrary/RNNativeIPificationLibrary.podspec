
Pod::Spec.new do |s|
  s.name         = "RNNativeIPificationLibrary"
  s.version      = "1.7"
  s.summary      = "RNNativeIPificationLibrary"
  s.description  = <<-DESC
                  RNNativeIPificationLibrary
                   DESC
  s.homepage     = "https://github.com/bvantagelimited/IPification-React-Native-Plugin#readme"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.v" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/author/RNNativeIPificationLibrary.git", :tag => "master" }
  s.source_files  = "ios/*.{h,m,mm,swift}"
  s.swift_version = "5.0"

  s.requires_arc = true
  s.dependency "React"
  s.vendored_frameworks = "ios/sdk/IPificationSDK.xcframework"
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }

end
