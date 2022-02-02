import * as React from 'react';

import { StyleSheet, View, Text, TextInput } from 'react-native';
import { multiply, NativeQRDisplayECCLevel, NativeQRDisplayView } from 'react-native-native-qr-display';

export default function App() {
  // const [result, setResult] = React.useState<number | undefined>();

  // React.useEffect(() => {
  //   multiply(3, 7).then(setResult);
  // }, []);
  const [qrText, setQRText] = React.useState('hello world');

  return (
    <View style={styles.container}>
      <View style={{ height: 50, justifyContent: 'center' }}>
        <Text>Enter Text to convert to QR</Text>
      </View>
      {/* <Text>Result: {result}</Text> */}
      <TextInput style={{ padding: 8, backgroundColor: '#FFF', borderWidth: 1, borderRadius: 4 }}
        onChangeText={setQRText}
      >
        {qrText}
      </TextInput>
      <NativeQRDisplayView
        style={{ backgroundColor: "green", flex: 1 }}
        stringData={qrText}
        ECC={NativeQRDisplayECCLevel.HIGH}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    // alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
