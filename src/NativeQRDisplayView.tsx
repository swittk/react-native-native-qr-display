import React from 'react';
import { requireNativeComponent, StyleProp, ViewStyle } from 'react-native';

// requireNativeComponent automatically resolves 'RNTMap' to 'RNTMapManager'

export enum NativeQRDisplayECCLevel {
  /** 
   * The QR Code can tolerate about  7% erroneous codewords 
   * Maximum length 2953 characters
  */
  LOW = 0,
  /** The QR Code can tolerate about 15% erroneous codewords 
   * Maximum length 2331 characters
  */
  MEDIUM,
  /** 
   * The QR Code can tolerate about 25% erroneous codewords 
   * Maximum length 1663 characters
  */
  QUARTILE,
  /** The QR Code can tolerate about 30% erroneous codewords 
   * Maximum length 1273 characters
  */
  HIGH,
}

export type NativeQRDisplayViewProps = {
  /**
   * The string data
   * Maximum length is depends on Error correction level.
   * If that occurs then the display will show nothing.
   */
  stringData?: string,
  ECC?: NativeQRDisplayECCLevel,
  style?: StyleProp<ViewStyle>
};
type NativeQRDisplayViewState = {};
const SKRNNativeQRDisplayView = requireNativeComponent<NativeQRDisplayViewProps>('SKRNNativeQRDisplayView');

/**
 * Native QR code display view
 * Note that stringData length must correspond to the ECC level (see NativeQRDisplayECCLevel definition for length limits)
 */
export class NativeQRDisplayView extends React.PureComponent<NativeQRDisplayViewProps, NativeQRDisplayViewState> {
  render() {
    const { stringData, ECC, style } = this.props;
    return <SKRNNativeQRDisplayView
      stringData={stringData}
      ECC={ECC}
      style={style}
    />
  }
}
