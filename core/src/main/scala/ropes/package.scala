import ropes.instances.DigitInstances

package object ropes extends DigitInstances {
  type Digit = Range['0', '9'] ConvertedTo Int
}
