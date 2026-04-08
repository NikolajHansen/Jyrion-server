import { useGetPlayersQuery, useGetPlayerStatusQuery, useSendCommandMutation, PlayerCommand } from './api/apiSlice'
import { useAppDispatch, useAppSelector } from './store/hooks'
import { setSelectedPlayer } from './store/uiSlice'
import { useServerEvents } from './hooks/useServerEvents'

function App() {
  useServerEvents()

  const { data: players = [], isLoading: playersLoading } = useGetPlayersQuery()
  const selectedPlayerId = useAppSelector((state) => state.ui.selectedPlayerId)
  const dispatch = useAppDispatch()

  const { data: status } = useGetPlayerStatusQuery(selectedPlayerId!, {
    skip: !selectedPlayerId,
    pollingInterval: 0,
  })

  const [sendCommand] = useSendCommandMutation()

  const send = (command: PlayerCommand) => {
    if (!selectedPlayerId) return
    sendCommand({ playerId: selectedPlayerId, command })
  }

  return (
    <div style={{ fontFamily: 'sans-serif', maxWidth: 640, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>🎵 Jyrion</h1>

      <section>
        <h2>Players</h2>
        {playersLoading && <p>Loading…</p>}
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {players.map((p) => (
            <li key={p.id} style={{ marginBottom: '0.5rem' }}>
              <button
                onClick={() => dispatch(setSelectedPlayer(p.id))}
                style={{
                  padding: '0.4rem 0.8rem',
                  background: selectedPlayerId === p.id ? '#0070f3' : '#eee',
                  color: selectedPlayerId === p.id ? '#fff' : '#000',
                  border: 'none',
                  borderRadius: 4,
                  cursor: 'pointer',
                }}
              >
                {p.name}
              </button>
            </li>
          ))}
        </ul>
      </section>

      {selectedPlayerId && (
        <section>
          <h2>Now Playing</h2>
          {status ? (
            <>
              <p>
                <strong>State:</strong>{' '}
                <span style={{ color: status.state === 'PLAYING' ? 'green' : status.state === 'PAUSED' ? 'orange' : 'gray' }}>
                  {status.state}
                </span>
              </p>
              {status.nowPlaying ? (
                <p>
                  🎵 <strong>{status.nowPlaying.title}</strong> — {status.nowPlaying.artist}
                  {status.nowPlaying.album && ` (${status.nowPlaying.album})`}
                </p>
              ) : (
                <p>Nothing queued</p>
              )}

              <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
                <button onClick={() => send({ type: 'play' })}>▶ Play</button>
                <button onClick={() => send({ type: 'pause' })}>⏸ Pause</button>
                <button onClick={() => send({ type: 'togglePause' })}>⏯ Toggle</button>
                <button onClick={() => send({ type: 'stop' })}>⏹ Stop</button>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <label htmlFor="volume">🔊 Volume: {status.muted ? '(muted)' : status.volume}</label>
                <input
                  id="volume"
                  type="range"
                  min={0}
                  max={100}
                  value={status.volume}
                  onChange={(e) => send({ type: 'setVolume', volume: Number(e.target.value) })}
                  style={{ width: 160 }}
                />
                <button onClick={() => send({ type: 'mute', muted: !status.muted })}>
                  {status.muted ? '🔇 Unmute' : '🔕 Mute'}
                </button>
              </div>
            </>
          ) : (
            <p>Loading status…</p>
          )}
        </section>
      )}
    </div>
  )
}

export default App

