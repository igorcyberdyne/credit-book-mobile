import SwiftUI
import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinInitIosKt.doInitKoinIos()// wrapper Kotlin exposé côté Swift
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}