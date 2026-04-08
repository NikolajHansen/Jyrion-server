import { useGetHealthQuery } from './api/apiSlice'
import { useAppDispatch, useAppSelector } from './store/hooks'
import { setSelectedPlayer } from './store/uiSlice'

function App() {
  const { data: health, isLoading, isError } = useGetHealthQuery()
  const selectedPlayer = useAppSelector((state) => state.ui.selectedPlayerId)
  const dispatch = useAppDispatch()

  return (
    <div style={{ fontFamily: 'sans-serif', maxWidth: 600, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>🎵 Jyrion</h1>

      <section>
        <h2>Server Health</h2>
        {isLoading && <p>Checking…</p>}
        {isError && <p style={{ color: 'red' }}>Backend unreachable</p>}
        {health && (
          <p style={{ color: 'green' }}>
            Status: <strong>{health.status}</strong> — {health.timestamp}
          </p>
        )}
      </section>

      <section>
        <h2>Selected Player</h2>
        <p>{selectedPlayer ?? <em>None selected</em>}</p>
        <button onClick={() => dispatch(setSelectedPlayer('player-1'))}>
          Select player-1
        </button>{' '}
        <button onClick={() => dispatch(setSelectedPlayer(null))}>
          Clear
        </button>
      </section>
    </div>
  )
}

export default App
