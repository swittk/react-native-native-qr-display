# react-native-native-qr-display

Native QR Code Display for React Native

This is based on the CPP implementation of @nayuki's brilliant [QR-Code-Generator](https://github.com/nayuki/QR-Code-generator)


## Installation

```sh
npm install react-native-native-qr-display
```

## Usage

```tsx
import { NativeQRDisplayECCLevel, NativeQRDisplayView } from 'react-native-native-qr-display';

render() {
    return <NativeQRDisplayView
        style={{ backgroundColor: "orange", flex: 1 }}
        // The string data
        stringData="My example QR Text"
        // The Error correction level. The maximum length of string depends on the error correction level (see typescript definitions for appropriate lengths)
        ECC={NativeQRDisplayECCLevel.HIGH}
    />
}
// ...

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
