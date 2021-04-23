// Code generated by go-swagger; DO NOT EDIT.

package models

// This file was generated by the swagger tool.
// Editing this file might prove futile when you re-run the swagger generate command

import (
	"context"

	"github.com/go-openapi/strfmt"
	"github.com/go-openapi/swag"
)

// DeployResponse deploy response
//
// swagger:model DeployResponse
type DeployResponse struct {

	// action
	Action string `json:"action,omitempty"`

	// info
	Info string `json:"info,omitempty"`
}

// Validate validates this deploy response
func (m *DeployResponse) Validate(formats strfmt.Registry) error {
	return nil
}

// ContextValidate validates this deploy response based on context it is used
func (m *DeployResponse) ContextValidate(ctx context.Context, formats strfmt.Registry) error {
	return nil
}

// MarshalBinary interface implementation
func (m *DeployResponse) MarshalBinary() ([]byte, error) {
	if m == nil {
		return nil, nil
	}
	return swag.WriteJSON(m)
}

// UnmarshalBinary interface implementation
func (m *DeployResponse) UnmarshalBinary(b []byte) error {
	var res DeployResponse
	if err := swag.ReadJSON(b, &res); err != nil {
		return err
	}
	*m = res
	return nil
}
