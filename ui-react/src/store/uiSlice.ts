import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface UiState {
  selectedPlayerId: string | null
  theme: 'dark' | 'light'
}

const initialState: UiState = {
  selectedPlayerId: null,
  theme: 'dark',
}

export const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    setSelectedPlayer(state, action: PayloadAction<string | null>) {
      state.selectedPlayerId = action.payload
    },
    setTheme(state, action: PayloadAction<'dark' | 'light'>) {
      state.theme = action.payload
    },
  },
})

export const { setSelectedPlayer, setTheme } = uiSlice.actions
export default uiSlice.reducer
