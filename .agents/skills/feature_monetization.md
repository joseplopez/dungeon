# Skill: Monetization & In-App Purchases

## Core Anchors
- Billing: `@BillingManager.kt`, `@PlayBillingManager.kt`
- Ads: `@AdManager.kt`, `@AdMobManager.kt`
- UI: `@ResourceShopDialog.kt`

## Data Flow & Cross-Feature Coupling
- Store Purchases: Verified Play Store purchases trigger currency grants (`addGold`, `addMagicite`) in `GameRepository`.
- Rewarded Video Ads: Completed video views trigger events in `DungeonViewModel` (floor battle revives or temporary Gold boost).

## Key Signatures & Execution Flow
1. `launchBillingFlow(activity, productId)`: Initiates Play Billing purchase sheet.
2. `onPurchaseVerified(purchase)`: Grants item in `GameRepository` and consumes purchase token.
3. `showRewardedAd(onRewardEarned: () -> Unit)`: Executes callback strictly after `onUserEarnedReward` fires.

## Feature Invariants
- Zero Ghost Grants: State modifications (`GameState`) MUST NOT occur until Play Store purchase token verification completes.
- Ad Callback Rule: Rewarded actions must strictly bind to verified completion callbacks; never award on ad start or dismiss.
