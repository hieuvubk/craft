package types // noalias

import (
	sdk "github.com/cosmos/cosmos-sdk/types"
	"github.com/cosmos/cosmos-sdk/x/auth/types"
)

// BankKeeper defines the contract needed to be fulfilled for banking and supply
// dependencies.
type BankKeeper interface {
	SendCoinsFromModuleToAccount(ctx sdk.Context, senderModule string, recipientAddr sdk.AccAddress, amt sdk.Coins) error
	MintCoins(ctx sdk.Context, name string, amt sdk.Coins) error
	GetBalance(ctx sdk.Context, addr sdk.AccAddress, denom string) sdk.Coin
	SendCoinsFromAccountToModule(ctx sdk.Context, senderAddr sdk.AccAddress, recipientModule string, amt sdk.Coins) error
	BurnCoins(ctx sdk.Context, moduleName string, amt sdk.Coins) error
}

// AccountKeeper defines the contract needed for AccountKeeper related APIs.
// Interface provides support to use non-sdk AccountKeeper for TxHandler's middlewares.
type AccountKeeper interface {
	GetModuleAccount(ctx sdk.Context, moduleName string) types.ModuleAccountI
	GetModuleAddress(name string) sdk.AccAddress
}
